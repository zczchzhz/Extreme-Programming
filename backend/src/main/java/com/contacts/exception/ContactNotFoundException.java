package com.contacts.exception;

/**
 * 联系人不存在异常
 */
public class ContactNotFoundException extends BusinessException {

    public ContactNotFoundException(Long contactId) {
        super("联系人不存在，ID: " + contactId);
    }

    public ContactNotFoundException(String message) {
        super(message);
    }
}