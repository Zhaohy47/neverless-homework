package com.example.demo.entity;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * exchange rate
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {
    /**
     * from currency
     */
    private String fromCurrency;
    /**
     * to currency
     */
    private String toCurrency;
    /**
     * exchange rate
     */
    private BigDecimal rate;
    /**
     * create time
     */
    private Long updateTime;
}
