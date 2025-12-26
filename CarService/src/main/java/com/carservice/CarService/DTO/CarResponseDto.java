package com.carservice.CarService.DTO;

import com.carservice.CarService.Entity.CarStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarResponseDto {

    private Integer id;
    private Integer ownerId;

    private String vehicleName;
    private String model;
    private Integer yearOfPurchase;
    private String registrationNumber;

    private String vehicleType;
    private String fuelType;
    private String transmission;

    private double mileage;
    private Integer seatingCapacity;
    private double pricePerHour;

    private CarStatus status;
    private double rating;
}
