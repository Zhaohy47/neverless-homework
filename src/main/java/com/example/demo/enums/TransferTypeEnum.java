package com.example.demo.enums;

import lombok.Getter;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@Getter
public enum TransferTypeEnum {
    WITHDRAW(0),
    DIRECT_TRANSFER(1),
    ;
    private final int value;

    TransferTypeEnum(int value) {
        this.value = value;
    }
}
