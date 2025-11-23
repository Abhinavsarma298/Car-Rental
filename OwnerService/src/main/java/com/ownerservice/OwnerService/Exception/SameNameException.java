package com.ownerservice.OwnerService.Exception;

public class SameNameException extends RuntimeException {
    public SameNameException(String message) {
        super(message);
    }

    public SameNameException(String message, Throwable cause) {
        super(message, cause);
    }
}

