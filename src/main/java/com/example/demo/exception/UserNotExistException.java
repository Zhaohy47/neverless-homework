package com.example.demo.exception;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
public class UserNotExistException extends RuntimeException {

    public static final String MESSAGE = "User does not exist";

    public UserNotExistException() {
        super(MESSAGE);
    }
}
