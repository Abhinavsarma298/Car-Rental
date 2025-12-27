package com.payment.PaymentService.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ------------------ PAYMENT NOT FOUND ------------------

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Object> handlePaymentNotFound(PaymentNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    // ------------------ INVALID PAYMENT ------------------

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<Object> handleInvalidPayment(InvalidPaymentException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    // ------------------ DUPLICATE PAYMENT ------------------

    @ExceptionHandler(DuplicatePaymentException.class)
    public ResponseEntity<Object> handleDuplicatePayment(DuplicatePaymentException ex) {

        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    // ------------------ PAYMENT ALREADY PROCESSED ------------------

    @ExceptionHandler(PaymentAlreadyProcessedException.class)
    public ResponseEntity<Object> handlePaymentAlreadyProcessed(PaymentAlreadyProcessedException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    // ------------------ GENERIC EXCEPTION ------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again later."
        );
    }

    // ------------------ RESPONSE BUILDER ------------------

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}