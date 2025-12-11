package com.bookingservice.BookingService.dto;

import com.bookingservice.BookingService.Entity.RentalState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long userId;
    private Long vehicleId;
    private LocalDateTime pickupDatetime;
    private LocalDateTime dropoffDatetime;
    private BigDecimal totalDistance;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal securityDeposit;
    private LocalDateTime bookedAt;
    private RentalState rentalState;
}
