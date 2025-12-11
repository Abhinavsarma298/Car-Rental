package com.bookingservice.BookingService.dto;

import jakarta.validation.constraints.*;
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
public class BookingRequest {
    @NotNull
    @Positive
    private Long userId;

    @NotNull
    @Positive
    private Long vehicleId;

    @NotNull
    private LocalDateTime pickupDatetime;

    @NotNull
    private LocalDateTime dropoffDatetime;

    // Optional: client-side estimate override
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal estimatedDistance;
}
