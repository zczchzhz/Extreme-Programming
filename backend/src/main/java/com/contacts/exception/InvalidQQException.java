package com.contacts.exception;

public class InvalidQQException extends BusinessException {
    public InvalidQQException(String qq) {
        super("QQ账号格式不正确: " + qq);
    }
}