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
public class DirectTransferRequest {
    /**
     * from user id
     */
    @NotNull
    private Long fromUserId;
    /**
     * to user id
     */
    @NotNull
    private Long toUserId;
    /**
     * amount
     */
    @NotBlank
    private String amount;
}
