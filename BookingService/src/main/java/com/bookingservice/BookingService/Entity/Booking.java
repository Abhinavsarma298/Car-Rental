package com.bookingservice.BookingService.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be positive")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Vehicle ID cannot be null")
    @Positive(message = "Vehicle ID must be positive")
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @NotNull(message = "Pickup datetime cannot be null")
    @Column(name = "pickup_datetime", nullable = false)
    private LocalDateTime pickupDatetime;

    @NotNull(message = "Dropoff datetime cannot be null")
    @Column(name = "dropoff_datetime", nullable = false)
    private LocalDateTime dropoffDatetime;

    @DecimalMin(value = "0.0", inclusive = true, message = "Distance cannot be negative")
    @Digits(integer = 8, fraction = 2)
    @Column(name = "total_distance", precision = 10, scale = 2)
    private BigDecimal totalDistance;

    @NotBlank(message = "Status cannot be blank")
    @Size(max = 50, message = "Status must be at most 50 characters")
    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total amount must be >= 0")
    @Digits(integer = 10, fraction = 2)
    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Security deposit must be >= 0")
    @Digits(integer = 10, fraction = 2)
    @Column(name = "security_deposit", precision = 12, scale = 2)
    private BigDecimal securityDeposit;

    @CreationTimestamp
    @Column(name = "booked_at", updatable = false, nullable = false)
    private LocalDateTime bookedAt;

    @AssertTrue(message = "Dropoff datetime must be after pickup datetime")
    @Transient
    public boolean isDropoffAfterPickup() {
        if (pickupDatetime == null || dropoffDatetime == null) {
            return true;
        }
        return dropoffDatetime.isAfter(pickupDatetime) || dropoffDatetime.isEqual(pickupDatetime);
    }
}
