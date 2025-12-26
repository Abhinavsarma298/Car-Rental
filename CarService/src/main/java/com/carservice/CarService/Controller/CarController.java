package com.carservice.CarService.Controller;

import com.carservice.CarService.DTO.CarRegisterDto;
import com.carservice.CarService.DTO.CarResponseDto;
import com.carservice.CarService.Service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * ADMIN: Register a new car (METHOD 2 – JSON as String)
     */
    @PostMapping(
            value = "/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CarResponseDto> registerCar(
            @RequestPart("carData") String carData,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        // Convert JSON string to DTO
        CarRegisterDto carDto =
                objectMapper.readValue(carData, CarRegisterDto.class);

        return ResponseEntity
                .status(201)
                .body(service.registerCar(carDto, image));
    }

    /**
     * USER: Get car by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getCarById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getCarById(id));
    }

    /**
     * USER: Get all cars by brand
     */
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<CarResponseDto>> getCarsByBrand(
            @PathVariable String brand
    ) {
        return ResponseEntity.ok(service.getCarsByBrand(brand));
    }

    /**
     * USER: Get all available cars
     */
    @GetMapping("/available")
    public ResponseEntity<List<CarResponseDto>> getAvailableCars() {
        return ResponseEntity.ok(service.getAvailableCars());
    }

    /**
     * ADMIN: Soft delete car
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCar(@PathVariable Integer id) {
        service.deleteCar(id);
        return ResponseEntity.ok("Car deleted successfully");
    }

    /**
     * BOOKING SERVICE: Lock car
     */
    @PutMapping("/{id}/book")
    public ResponseEntity<String> markCarAsBooked(@PathVariable Integer id) {
        service.markCarAsBooked(id);
        return ResponseEntity.ok("Car marked as BOOKED");
    }

    /**
     * BOOKING SERVICE: Release car
     */
    @PutMapping("/{id}/release")
    public ResponseEntity<String> markCarAsAvailable(@PathVariable Integer id) {
        service.markCarAsAvailable(id);
        return ResponseEntity.ok("Car marked as AVAILABLE");
    }

    /**
     * USER: Get car image
     */
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getCarImage(@PathVariable Integer id) {
        return service.getCarImage(id);
    }
}
