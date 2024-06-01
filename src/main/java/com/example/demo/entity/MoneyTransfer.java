package com.example.demo.entity;

import com.example.demo.enums.TransferStatusEnum;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoneyTransfer {
    /**
     * transfer id -- UUID
     * transfer id is withdrawId for withdraw case
     */
    private UUID transferId;
    /**
     * user id
     */
    private Long userId;
    /**
     * transfer type
     * @see TransferStatusEnum
     */
    private Integer transferType;
    /**
     * target info
     * address for withdraw
     * to user id for direct transfer
     */
    private String targetInfo;
    /**
     * from currency
     */
    private String fromCurrency;
    /**
     * from amount
     */
    private BigDecimal fromAmount;
    /**
     * to currency
     */
    private String toCurrency;
    /**
     * to amount
     */
    private BigDecimal toAmount;
    /**
     * exchange rate
     */
    private BigDecimal exchangeRate;
    /**
     * status
     * @see com.example.demo.enums.TransferStatusEnum
     */
    private Integer status;
    /**
     * create time
     */
    private Long createTime;
    /**
     * update time
     */
    private Long statusTime;
}
