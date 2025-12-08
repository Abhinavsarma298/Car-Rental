package com.carservice.CarService.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarRegisterDto {
    private Integer ownerId;
    private String vehicleName;
    private String model;
    private Integer yearOfPurchase;
    private String registrationNumber;
    private String vehicleType;
    private String fuelType;
    private String transmission;
    private Double mileage;
    private Integer seatingCapacity;
    private Double pricePerHour;
    private Boolean isAvailable;
    private Double rating;

    // no-args constructor, getters, setters (use Lombok if you prefer)
}

