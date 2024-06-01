package com.example.demo.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaohangyu
 * @date 1/6/24
 *
 */
@Getter
public enum TransferStatusEnum {

    PROCESSING(0, "processing"),
    COMPLETED(1, "completed"),
    CANCELED(2, "canceled"),
    FAILED(-1, "failed");

    private final int value;
    private final String desc;

    TransferStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
    private static final Map<Integer, TransferStatusEnum> STATUS_MAP = Arrays.stream(TransferStatusEnum.values())
            .collect(Collectors.toMap(TransferStatusEnum::getValue, Function.identity()));

    public static TransferStatusEnum fromValue(int value) {
        if (!STATUS_MAP.containsKey(value)) {
            throw new IllegalArgumentException("invalid transfer status value: " + value);
        }
        return STATUS_MAP.get(value);
    }


}
