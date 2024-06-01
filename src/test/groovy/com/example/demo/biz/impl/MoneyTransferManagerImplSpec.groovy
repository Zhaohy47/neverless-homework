package com.example.demo.biz.impl


import com.example.demo.entity.MoneyTransfer
import com.example.demo.entity.UserAccount
import com.example.demo.enums.TransferStatusEnum
import com.example.demo.enums.TransferTypeEnum
import com.example.demo.external.WithdrawalService
import com.example.demo.persistent.AccountService
import com.example.demo.persistent.ExchangeRateService
import com.example.demo.persistent.MoneyTransferService
import com.example.demo.persistent.impl.MoneyTransferServiceImpl
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
class MoneyTransferManagerImplSpec extends Specification {

    @Subject
    MoneyTransferManagerImpl moneyTransferManager
    AccountService accountService
    MoneyTransferService moneyTransferService
    ExchangeRateService exchangeRateService
    WithdrawalService withdrawalService
    def setup() {
        accountService = Mock(AccountService)
        moneyTransferService = new MoneyTransferServiceImpl()
        exchangeRateService = Mock(ExchangeRateService)
        withdrawalService = Mock(WithdrawalService)
        moneyTransferManager = new MoneyTransferManagerImpl(accountService, moneyTransferService, exchangeRateService, withdrawalService)
    }

    @Unroll
    def "directTransfer transfers amount correctly"() {
        given:
        UUID transferId = UUID.randomUUID()
        UserAccount fromAccount = UserAccount.builder()
                .userId(1L)
                .balance(new BigDecimal("100"))
                .freezeBalance(new BigDecimal("0"))
                .activeBalance(new BigDecimal("100"))
                .currency("USD")
                .accountStatus(0)
                .lastLoginTime(System.currentTimeMillis())
                .build();
        UserAccount toAccount = UserAccount.builder()
                        .userId(2L)
                        .balance(new BigDecimal("100"))
                        .freezeBalance(new BigDecimal("0"))
                        .activeBalance(new BigDecimal("100"))
                        .currency("EUR")
                        .accountStatus(0)
                        .lastLoginTime(System.currentTimeMillis())
                        .build();
        BigDecimal amount = new BigDecimal("10")
        BigDecimal exchangeRate = new BigDecimal("0.85")

        and:
        accountService.getUserAccount(1L) >> Optional.of(fromAccount)
        accountService.getUserAccount(2L) >> Optional.of(toAccount)
        exchangeRateService.getExchangeRate("USD", "EUR") >> Optional.of(exchangeRate)
        when:
        String result = moneyTransferManager.directTransfer(1L, 2L, amount)

        then:
        1 * accountService.directDeductBalance(1L, amount)
        1 * accountService.directAddBalance(2L, amount.multiply(exchangeRate))
        result!= null
    }


    @Unroll
    def "withDraw initiates withdrawal correctly"() {
        given:
        Long userId = 1L
        WithdrawalService.Address address = new WithdrawalService.Address("test address")
        BigDecimal amount = new BigDecimal("100")
        String targetCurrency = "EUR"
        UserAccount fromAccount = UserAccount.builder()
                .userId(userId)
                .balance(new BigDecimal("100"))
                .freezeBalance(new BigDecimal("0"))
                .activeBalance(new BigDecimal("100"))
                .currency("USD")
                .accountStatus(0)
                .lastLoginTime(System.currentTimeMillis())
                .build();
        BigDecimal exchangeRate = new BigDecimal("0.85")
        UUID transferId = UUID.randomUUID()

        and:
        accountService.getUserAccount(userId) >> Optional.of(fromAccount)
        exchangeRateService.getExchangeRate(fromAccount.getCurrency(), targetCurrency) >> Optional.of(exchangeRate)

        when:
        String result = moneyTransferManager.withDraw(userId, address, amount, targetCurrency)

        then:
        1 * accountService.freezeAmount(userId, amount)
        1 * withdrawalService.requestWithdrawal(_, _, _)
        result != null
    }

    @Unroll
    def "getTransferStatus returns correct status"() {
        given:
        UUID transferId = UUID.randomUUID()
        MoneyTransfer transfer = new MoneyTransfer(transferId, 1L, TransferTypeEnum.DIRECT_TRANSFER.getValue(), "2", "USD", new BigDecimal("10"), "EUR", new BigDecimal("8.5"), new BigDecimal("0.85"), TransferStatusEnum.COMPLETED.getValue(), 0L, 0L)
        moneyTransferService.MOCK_DB.put(transferId, transfer)

        when:
        TransferStatusEnum result = moneyTransferManager.getTransferStatus(transferId.toString())

        then:
        result == TransferStatusEnum.COMPLETED
    }

}
