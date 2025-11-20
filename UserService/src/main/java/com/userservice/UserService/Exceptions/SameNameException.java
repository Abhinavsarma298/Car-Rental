package com.userservice.UserService.Exceptions;

public class SameNameException extends RuntimeException{
    public SameNameException(String message) {
        super(message);
    }
}
