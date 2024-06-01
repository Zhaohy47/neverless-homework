package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccount {
    /**
     * user id
     */
    private Long userId;
    /**
     * balance
     */
    private BigDecimal balance;
    /**
     * freeze balance
     */
    private BigDecimal freezeBalance;
    /**
     * active balance = balance - freeze balance
     */
    private BigDecimal activeBalance;
    /**
     * currency code
     */
    private String currency;
    /**
     * 0: normal, 1: frozen
     * @see com.example.demo.enums.AccountStatusEnum
     */
    private Integer accountStatus;
    /**
     * last login time
     */
    private Long lastLoginTime;
}
