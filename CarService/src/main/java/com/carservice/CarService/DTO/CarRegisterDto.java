package com.carservice.CarService.DTO;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.checkerframework.checker.index.qual.Positive;

@Getter
@Setter
public class CarRegisterDto {

    @NotNull()
    private Integer ownerId;

    @NotBlank(message = "Vehicle name is required")
    private String vehicleName;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull()
    @Min(value = 1990, message = "Year must be valid")
    private Integer yearOfPurchase;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @NotBlank(message = "Fuel type is required")
    private String fuelType;

    @NotBlank(message = "Transmission is required")
    private String transmission;

    @Positive()
    private double mileage;

    @NotNull()
    @Min(value = 1, message = "Seating capacity must be at least 1")
    private Integer seatingCapacity;

    @Positive()
    private double pricePerHour;
}
