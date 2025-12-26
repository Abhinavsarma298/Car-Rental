package com.bookingservice.BookingService.Exception;

public class VehicleAlreadyBookedException extends RuntimeException {
    public VehicleAlreadyBookedException(String message) {
        super(message);
    }
}
