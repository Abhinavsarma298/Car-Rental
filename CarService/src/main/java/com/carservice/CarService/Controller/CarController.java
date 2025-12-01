package com.carservice.CarService.Controller;

import com.carservice.CarService.Entity.CarServiceEntity;
import com.carservice.CarService.Service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarService service;



    // Register a new car
    @PostMapping("/register")
    public CarServiceEntity register(
            @RequestParam("ownerId") Integer ownerId,
            @RequestParam("vehicleName") String vehicleName,
            @RequestParam("model") String model,
            @RequestParam("yearOfPurchase") Integer yearOfPurchase,
            @RequestParam("registrationNumber") String registrationNumber,
            @RequestParam("vehicleType") String vehicleType,
            @RequestParam("fuelType") String fuelType,
            @RequestParam("transmission") String transmission,
            @RequestParam("mileage") double mileage,
            @RequestParam("seatingCapacity") Integer seatingCapacity,
            @RequestParam("pricePerHour") double pricePerHour,
            @RequestParam("isAvailable") Boolean isAvailable,
            @RequestParam(value = "rating", defaultValue = "0") double rating,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {

        CarServiceEntity car = new CarServiceEntity();

        car.setOwnerId(ownerId);
        car.setVehicleName(vehicleName);
        car.setModel(model);
        car.setYearOfPurchase(yearOfPurchase);
        car.setRegistrationNumber(registrationNumber);
        car.setVehicleType(vehicleType);
        car.setFuelType(fuelType);
        car.setTransmission(transmission);
        car.setMileage(mileage);
        car.setSeatingCapacity(seatingCapacity);
        car.setPricePerHour(pricePerHour);
        car.setIsAvailable(isAvailable);
        car.setRating(rating);

        if (imageFile != null && !imageFile.isEmpty()) {
            car.setImage(imageFile.getBytes());
        }

        return service.register(car);
    }


    // Get ALL cars by brand (vehicleName)
    @GetMapping("/brand/{brand}")
    public List<CarServiceEntity> getCarsByBrand(@PathVariable String brand) {
        return service.getCarsByBrand(brand);
    }

    // Get car by id
    @GetMapping("/id/{id}")
    public CarServiceEntity getCarById(@PathVariable Integer id) {
        return service.getCarById(id);
    }

    @DeleteMapping("/delete/{id}")
    public String removeCarById(@PathVariable Integer id) {

        return service.removeCarById(id);

    }

}
