package com.carservice.CarService.Service;

import com.carservice.CarService.CarRepo.CarRepo;
import com.carservice.CarService.DTO.CarRegisterDto;
import com.carservice.CarService.DTO.CarResponseDto;
import com.carservice.CarService.DTO.OwnerServerEntity;
import com.carservice.CarService.Entity.CarServiceEntity;
import com.carservice.CarService.Entity.CarStatus;
import com.carservice.CarService.Exception.CarAlreadyExistsException;
import com.carservice.CarService.Exception.CarNotFoundException;
import com.carservice.CarService.Exception.InvalidCarStateException;
import com.carservice.CarService.FeignClient.OwnerServiceClient;
import com.ownerservice.OwnerService.Exception.OwnerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.BeanUtils;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
@Service
public class CarService {

    @Autowired
    private CarRepo repo;

    @Autowired
    private OwnerServiceClient ownerServiceClient;

    /* ===================== REGISTER CAR ===================== */
    public CarResponseDto registerCar(
            CarRegisterDto dto,
            MultipartFile image
    ) throws IOException {

        // Validate owner existence
        try {
            ownerServiceClient.getOwnerById(dto.getOwnerId());
        } catch (Exception e) {
            throw new OwnerNotFoundException(
                    "Owner not found with ID - " + dto.getOwnerId()
            );
        }

        // Check duplicate registration number
        repo.findByRegistrationNumber(dto.getRegistrationNumber())
                .ifPresent(car -> {
                    throw new CarAlreadyExistsException(
                            "Car already exists with Registration Number - "
                                    + dto.getRegistrationNumber()
                    );
                });

        // Map DTO → Entity
        CarServiceEntity car = new CarServiceEntity();
        BeanUtils.copyProperties(dto, car);

        car.setStatus(CarStatus.AVAILABLE);
        car.setDeleted(false);

        if (image != null && !image.isEmpty()) {
            car.setImage(image.getBytes());
        }

        CarServiceEntity savedCar = repo.save(car);

        // Map Entity → Response DTO
        CarResponseDto response = new CarResponseDto();
        BeanUtils.copyProperties(savedCar, response);

        return response;
    }

    /* ===================== GET CAR BY ID ===================== */
    public CarResponseDto getCarById(Integer id) {

        CarServiceEntity car = repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new CarNotFoundException(
                                "Car not found with ID - " + id
                        )
                );

        CarResponseDto response = new CarResponseDto();
        BeanUtils.copyProperties(car, response);
        return response;
    }

    /* ===================== GET CARS BY BRAND ===================== */
    public List<CarResponseDto> getCarsByBrand(String brand) {

        List<CarServiceEntity> cars =
                repo.findByVehicleNameIgnoreCaseAndDeletedFalse(brand);

        if (cars.isEmpty()) {
            throw new CarNotFoundException(
                    "No cars found with Brand - " + brand
            );
        }

        return cars.stream().map(car -> {
            CarResponseDto dto = new CarResponseDto();
            BeanUtils.copyProperties(car, dto);
            return dto;
        }).toList();
    }

    /* ===================== GET AVAILABLE CARS ===================== */
    public List<CarResponseDto> getAvailableCars() {

        List<CarServiceEntity> cars =
                repo.findByStatusAndDeletedFalse(CarStatus.AVAILABLE);

        if (cars.isEmpty()) {
            throw new CarNotFoundException("No available cars found");
        }

        return cars.stream().map(car -> {
            CarResponseDto dto = new CarResponseDto();
            BeanUtils.copyProperties(car, dto);
            return dto;
        }).toList();
    }

    /* ===================== SOFT DELETE ===================== */
    public void deleteCar(Integer id) {

        CarServiceEntity car = repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new CarNotFoundException(
                                "Car not found with ID - " + id
                        )
                );

        car.setDeleted(true);
        car.setStatus(CarStatus.INACTIVE);
        repo.save(car);
    }

    /* ===================== BOOKING: LOCK CAR ===================== */
    public void markCarAsBooked(Integer carId) {

        CarServiceEntity car = repo.findByIdAndDeletedFalse(carId)
                .orElseThrow(() ->
                        new CarNotFoundException(
                                "Car not found with ID - " + carId
                        )
                );

        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new InvalidCarStateException(
                    "Car is not available for booking"
            );
        }

        car.setStatus(CarStatus.BOOKED);
        repo.save(car);
    }

    /* ===================== BOOKING: RELEASE CAR ===================== */
    public void markCarAsAvailable(Integer carId) {

        CarServiceEntity car = repo.findByIdAndDeletedFalse(carId)
                .orElseThrow(() ->
                        new CarNotFoundException(
                                "Car not found with ID - " + carId
                        )
                );

        car.setStatus(CarStatus.AVAILABLE);
        repo.save(car);
    }

    /* ===================== GET CAR IMAGE ===================== */
    public ResponseEntity<byte[]> getCarImage(Integer id) {

        CarServiceEntity car = repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new CarNotFoundException(
                                "Car not found with ID - " + id
                        )
                );

        if (car.getImage() == null) {
            throw new CarNotFoundException("Car image not found");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(car.getImage());
    }
}
