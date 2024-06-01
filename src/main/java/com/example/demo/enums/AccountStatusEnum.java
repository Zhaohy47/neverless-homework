package com.example.demo.enums;

import lombok.Getter;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@Getter
public enum AccountStatusEnum {
    NORMAL(0),
    FROZEN(1);

    private final int value;

    AccountStatusEnum(int value) {
        this.value = value;
    }
}
