package com.carservice.CarService.Exception;

public class InvalidCarStateException extends RuntimeException {

    public InvalidCarStateException(String message) {
        super(message);
    }
}
