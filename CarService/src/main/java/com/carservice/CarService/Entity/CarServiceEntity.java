package com.carservice.CarService.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vehicles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "registration_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /* ================= OWNER ================= */
    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    /* ================= VEHICLE DETAILS ================= */
    @Column(name = "vehicle_name", nullable = false, length = 100)
    private String vehicleName;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "year_of_purchase", nullable = false)
    private Integer yearOfPurchase;

    @Column(name = "registration_number", nullable = false, length = 100, unique = true)
    private String registrationNumber;

    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;

    @Column(name = "fuel_type", nullable = false, length = 50)
    private String fuelType;

    @Column(name = "transmission", nullable = false, length = 50)
    private String transmission;

    @Column(name = "mileage", nullable = false)
    private double mileage;

    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity;

    @Column(name = "price_per_hour", nullable = false)
    private double pricePerHour;

    /* ================= STATUS (REAL WORLD) ================= */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    /* ================= SOFT DELETE ================= */
    @Column(nullable = false)
    private Boolean deleted = false;

    /* ================= RATING ================= */
    @Column(name = "rating", nullable = false)
    private double rating = 0.0;

    /* ================= IMAGE ================= */
    @Lob
    @Column(name = "image", columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    /* ================= AUDIT ================= */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
