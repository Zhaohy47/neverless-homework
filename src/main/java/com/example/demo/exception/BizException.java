package com.example.demo.exception;

/**
 * @author zhaohangyu
 * @date 1/6/24
 *
 * Handles exceptions in the business logic. Shouldn't happen.
 */
public class BizException extends RuntimeException{

        public BizException(String message) {
            super(message);
        }
}
