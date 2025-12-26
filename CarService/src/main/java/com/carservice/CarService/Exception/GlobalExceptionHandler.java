package com.carservice.CarService.Exception;

import com.carservice.CarService.Exception.InvalidCarStateException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ownerservice.OwnerService.Exception.OwnerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OwnerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOwnerNotFound(
            OwnerNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    @ExceptionHandler(CarAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCarAlreadyExists(
            CarAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        LocalDateTime.now(),
                        HttpStatus.CONFLICT.value()
                ));
    }

    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCarNotFound(
            CarNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    // CAR-BOOK-02 — Already booked
    @ExceptionHandler(InvalidCarStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCarState(
            InvalidCarStateException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    // CAR-REG-06 — Missing carData
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingPart(
            MissingServletRequestPartException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "Missing required part: " + ex.getRequestPartName(),
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    // CAR-REG-07 — Invalid JSON
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessing(
            JsonProcessingException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "Invalid JSON format in carData",
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
            HttpMessageNotReadableException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "Invalid JSON format in carData",
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    // Wrong or missing Content-Type
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex) {

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ErrorResponse(
                        "Content-Type must be multipart/form-data",
                        LocalDateTime.now(),
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()
                ));
    }

    // Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "Internal server error",
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }
}
