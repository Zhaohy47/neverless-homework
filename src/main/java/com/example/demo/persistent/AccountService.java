package com.example.demo.persistent;

import com.example.demo.entity.UserAccount;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
public interface AccountService {
    /**
     * Get user account by userId
     * @param userId
     * @return
     */
    Optional<UserAccount> getUserAccount(Long userId);

    /**
     * Freeze amount from user account
     * @param userId
     * @param amount
     */
    void freezeAmount(Long userId, BigDecimal amount);
    /**
     * Unfreeze amount from user account
     * @param userId
     * @param amount
     */
    void unfreezeAmount(Long userId, BigDecimal amount);

    /**
     * Deduct amount from user account
     * @param userId
     * @param amount
     */
    void deductAmount(Long userId, BigDecimal amount);

    /**
     * Directly add balance. used for direct transfer
     * @param userId
     * @param amount
     */
    void directAddBalance(Long userId, BigDecimal amount);

    /**
     * Directly deduct balance. used for direct transfer
     * @param fromUserId
     * @param amount
     */
    void directDeductBalance(Long fromUserId, BigDecimal amount);
}
