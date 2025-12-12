package com.contacts.exception;

/**
 * 电话号码重复异常
 */
public class DuplicatePhoneException extends BusinessException {

    public DuplicatePhoneException(String phone) {
        super("电话号码已存在: " + phone);
    }

    public DuplicatePhoneException(String message, Throwable cause) {
        super(message, cause);
    }
}