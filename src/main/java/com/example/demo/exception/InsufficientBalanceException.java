package com.example.demo.exception;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
public class InsufficientBalanceException extends RuntimeException{

        public static final String MESSAGE = "Insufficient balance";

        public InsufficientBalanceException() {
            super(MESSAGE);
        }
}
