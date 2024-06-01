package com.example.demo.dto;

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
public class TransferAmountDto {
    private BigDecimal amount;
    private String currency;
}
