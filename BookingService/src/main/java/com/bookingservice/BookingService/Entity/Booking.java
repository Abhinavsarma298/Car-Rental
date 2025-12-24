package com.bookingservice.BookingService.Entity;

import jakarta.persistence.*;
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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "pickup_datetime", nullable = false)
    private LocalDateTime pickupDatetime;

    @Column(name = "dropoff_datetime", nullable = false)
    private LocalDateTime dropoffDatetime;

    @Column(name = "total_distance")
    private BigDecimal totalDistance;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "security_deposit")
    private BigDecimal securityDeposit;

    @CreationTimestamp
    @Column(name = "booked_at", updatable = false, nullable = false)
    private LocalDateTime bookedAt;
}
