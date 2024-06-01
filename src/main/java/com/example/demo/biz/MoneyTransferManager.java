package com.example.demo.biz;

import com.example.demo.enums.TransferStatusEnum;
import com.example.demo.external.WithdrawalService;

import java.math.BigDecimal;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
public interface MoneyTransferManager {
    /**
     * Transfer money from one user to another
     * @param fromUserId
     * @param toUserId
     * @param amount
     * @return Money transfer id
     */
    String directTransfer(Long fromUserId, Long toUserId, BigDecimal amount);

    /**
     * Withdraw money from user account
     * @param userId
     * @param address withdrawal address
     * @param amount amount to withdraw from user account
     * @param targetCurrency target currency
     * @return transfer id aka withdraw id
     */
    String withDraw(Long userId, WithdrawalService.Address address, BigDecimal amount, String targetCurrency);

    /**
     * Get transfer status
     * @param transferId
     * @return
     */
    TransferStatusEnum getTransferStatus(String transferId);

}
