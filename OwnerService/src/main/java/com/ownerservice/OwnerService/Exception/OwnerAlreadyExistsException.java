package com.ownerservice.OwnerService.Exception;

public class OwnerAlreadyExistsException extends RuntimeException {
    public OwnerAlreadyExistsException(String message) {
        super(message);
    }

    public OwnerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

