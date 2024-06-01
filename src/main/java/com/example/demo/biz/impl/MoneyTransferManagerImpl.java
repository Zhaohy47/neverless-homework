package com.example.demo.biz.impl;

import com.example.demo.biz.MoneyTransferManager;
import com.example.demo.dto.TransferAmountDto;
import com.example.demo.entity.MoneyTransfer;
import com.example.demo.entity.UserAccount;
import com.example.demo.entity.WithdrawRequest;
import com.example.demo.enums.TransferStatusEnum;
import com.example.demo.enums.TransferTypeEnum;
import com.example.demo.external.WithdrawalService;
import com.example.demo.persistent.AccountService;
import com.example.demo.persistent.ExchangeRateService;
import com.example.demo.persistent.MoneyTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.example.demo.external.WithdrawalService.WithdrawalState.COMPLETED;
import static com.example.demo.external.WithdrawalService.WithdrawalState.FAILED;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@Service
@Slf4j
public class MoneyTransferManagerImpl implements MoneyTransferManager {

    private final AccountService accountService;

    private final MoneyTransferService moneyTransferService;

    private final ExchangeRateService exchangeRateService;

    private final WithdrawalService withdrawalService;

    private static final CompletionService<UUID> executorService = new ExecutorCompletionService<>(
            new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                    Runtime.getRuntime().availableProcessors() * 2,
                    60,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>())
    );

    public static final String userDoesNotExist = "user does not exist";


    @Autowired
    public MoneyTransferManagerImpl(AccountService accountService,
                                    MoneyTransferService moneyTransferService,
                                    ExchangeRateService exchangeRateService,
                                    WithdrawalService withdrawalService) {
        this.accountService = accountService;
        this.moneyTransferService = moneyTransferService;
        this.exchangeRateService = exchangeRateService;
        this.withdrawalService = withdrawalService;
    }


    @Override
    public String directTransfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        UserAccount fromAccount = accountService.getUserAccount(fromUserId).orElseThrow(() -> new IllegalArgumentException(userDoesNotExist));
        UserAccount toAccount = accountService.getUserAccount(toUserId).orElseThrow(() -> new IllegalArgumentException(userDoesNotExist));
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(fromAccount.getCurrency(), toAccount.getCurrency())
                .orElseThrow(() -> new IllegalArgumentException("exchange rate does not exist"));
        MoneyTransfer moneyTransfer = constructMoneyTransfer(fromAccount, toAccount, amount, exchangeRate);
        doInTransaction(() -> {
            accountService.directDeductBalance(fromUserId, moneyTransfer.getFromAmount());
            accountService.directAddBalance(toUserId, moneyTransfer.getToAmount());
            moneyTransferService.insert(moneyTransfer);
        });
        return moneyTransfer.getTransferId().toString();
    }


    @Override
    public String withDraw(Long userId, WithdrawalService.Address address, BigDecimal amount, String targetCurrency) {
        UserAccount fromAccount = accountService.getUserAccount(userId).orElseThrow(() -> new IllegalArgumentException("user does not exist"));
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(fromAccount.getCurrency(), targetCurrency)
                .orElseThrow(() -> new IllegalArgumentException("exchange rate does not exist"));
        MoneyTransfer moneyTransfer = constructMoneyTransfer(fromAccount, address, amount, targetCurrency, exchangeRate);
        return submitWithdrawal(moneyTransfer);
    }

    @Override
    public TransferStatusEnum getTransferStatus(String transferId) {
        return TransferStatusEnum.fromValue(
                moneyTransferService.getByTransferId(UUID.fromString(transferId)).getStatus());
    }

    private String submitWithdrawal(MoneyTransfer moneyTransfer) {
        doInTransaction(() -> {
            moneyTransferService.insert(moneyTransfer);
            accountService.freezeAmount(moneyTransfer.getUserId(), moneyTransfer.getFromAmount());
        });
        try {
            WithdrawRequest request = new WithdrawRequest(moneyTransfer);
            withdrawalService.requestWithdrawal(
                    request.getWithdrawalId(),
                    request.getAddress(),
                    request.getAmount());
            executorService.submit(() -> fetchWithdrawState(request));
        } catch (IllegalStateException e) {
            log.info("withdrawal request failed, unfreeze amount, transferId: {}", moneyTransfer.getTransferId());
            doInTransaction(() -> {
                accountService.unfreezeAmount(moneyTransfer.getUserId(), moneyTransfer.getFromAmount());
                moneyTransferService.updateStatus(moneyTransfer.getTransferId(), TransferStatusEnum.FAILED);
            });
        }
        return moneyTransfer.getTransferId().toString();
    }

    private UUID fetchWithdrawState(WithdrawRequest request) {
        while (true) {
            WithdrawalService.WithdrawalState newStatus = withdrawalService.getRequestState(request.getWithdrawalId());
            if (newStatus == COMPLETED) {
                doInTransaction(() -> {
                    accountService.deductAmount(request.getUserId(), request.getAmount().getAmount());
                    moneyTransferService.updateStatus(
                            request.getWithdrawalId().value(),
                            TransferStatusEnum.COMPLETED);
                });
                log.info("withdrawal request completed, transferId: {}, userId: {}, amount:{}",
                        request.getWithdrawalId().value(),
                        request.getUserId(),
                        request.getAmount().getAmount());

                return request.getWithdrawalId().value();
            } else if (newStatus == FAILED) {
                doInTransaction(() -> {
                    accountService.unfreezeAmount(request.getUserId(), request.getAmount().getAmount());
                    moneyTransferService.updateStatus(request.getWithdrawalId().value(),
                            TransferStatusEnum.FAILED);
                });
                log.error("withdrawal request failed, transferId: {}, userId: {}, amount:{}",
                        request.getWithdrawalId().value(),
                        request.getUserId(),
                        request.getAmount().getAmount());
                return request.getWithdrawalId().value();
            }
        }
    }

    private MoneyTransfer constructMoneyTransfer(UserAccount fromAccount, UserAccount toAccount, BigDecimal amount, BigDecimal exchangeRate) {
        return MoneyTransfer.builder().transferType(TransferTypeEnum.DIRECT_TRANSFER.getValue())
                .targetInfo(String.valueOf(toAccount.getUserId()))
                .fromCurrency(fromAccount.getCurrency())
                .toCurrency(toAccount.getCurrency())
                .fromAmount(amount)
                .toAmount(amount.multiply(exchangeRate))
                .exchangeRate(exchangeRate)
                .status(TransferStatusEnum.COMPLETED.getValue())
                .createTime(System.currentTimeMillis())
                .statusTime(System.currentTimeMillis())
                .build();
    }

    private MoneyTransfer constructMoneyTransfer(UserAccount fromAccount,
                                                 WithdrawalService.Address address,
                                                 BigDecimal amount,
                                                 String targetCurrency,
                                                 BigDecimal exchangeRate) {
        return MoneyTransfer.builder().transferType(TransferTypeEnum.WITHDRAW.getValue())
                .targetInfo(address.value())
                .userId(fromAccount.getUserId())
                .fromCurrency(fromAccount.getCurrency())
                .toCurrency(targetCurrency)
                .fromAmount(amount)
                .toAmount(amount.multiply(exchangeRate))
                .exchangeRate(exchangeRate)
                .status(TransferStatusEnum.PROCESSING.getValue())
                .createTime(System.currentTimeMillis())
                .statusTime(System.currentTimeMillis())
                .build();
    }

    /**
     * This method should be transactional. But for simplicity, we ignore it here.
     * @param runnable
     */
    private void doInTransaction(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
