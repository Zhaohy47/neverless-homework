package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithDrawRequest {
    /**
     * user id
     */
    @NotNull
    private Long userId;
    /**
     * address
     */
    @NotBlank
    private String address;
    /**
     * amount
     */
    @NotNull
    private String amount;
    /**
     * target currency
     */
    @NotNull
    private String targetCurrency;
}
