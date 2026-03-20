package com.userservice.UserService.Exceptions;

public class AccountDeletedException extends RuntimeException {

    public AccountDeletedException(String message) {
        super(message);
    }
}