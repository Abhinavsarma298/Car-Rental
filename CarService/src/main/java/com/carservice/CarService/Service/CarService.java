package com.carservice.CarService.Service;

import com.carservice.CarService.CarRepo.CarRepo;
import com.carservice.CarService.DTO.OwnerServerEntity;
import com.carservice.CarService.Entity.CarServiceEntity;
import com.carservice.CarService.Entity.CarStatus;
import com.carservice.CarService.Exception.CarAlreadyExistsException;
import com.carservice.CarService.Exception.CarNotFoundException;
import com.carservice.CarService.Exception.InvalidCarStateException;
import com.carservice.CarService.FeignClient.OwnerServiceClient;
import com.ownerservice.OwnerService.Exception.OwnerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    @Autowired
    private CarRepo repo;

    @Autowired
    private OwnerServiceClient ownerServiceClient;

    /* ===================== REGISTER CAR ===================== */
    public CarServiceEntity register(CarServiceEntity car) {

        // ✔ Validate owner existence
        try {
            ownerServiceClient.getOwnerById(car.getOwnerId());
        } catch (Exception e) {
            throw new OwnerNotFoundException(
                    "Owner not found with ID - " + car.getOwnerId()
            );
        }

        // ✔ Check duplicate registration number
        Optional<CarServiceEntity> existingCar =
                repo.findByRegistrationNumber(car.getRegistrationNumber());

        if (existingCar.isPresent()) {
            throw new CarAlreadyExistsException(
                    "Car already exists with Registration Number - "
                            + car.getRegistrationNumber()
            );
        }

        // ✔ Default values (IMPORTANT)
        car.setStatus(CarStatus.AVAILABLE);
        car.setDeleted(false);

        return repo.save(car);
    }

    /* ===================== GET CARS BY OWNER ===================== */
    public List<CarServiceEntity> getCarsByOwnerId(Integer ownerId) {

        // ✔ Validate owner existence
        try {
            ownerServiceClient.getOwnerById(ownerId);
        } catch (Exception e) {
            throw new OwnerNotFoundException(
                    "Owner not found with ID - " + ownerId
            );
        }

        List<CarServiceEntity> cars =
                repo.findByOwnerIdAndDeletedFalse(ownerId);

        if (cars.isEmpty()) {
            throw new CarNotFoundException(
                    "No cars found for Owner ID - " + ownerId
            );
        }
        return cars;
    }

    /* ===================== GET CARS BY BRAND ===================== */
    public List<CarServiceEntity> getCarsByBrand(String brand) {

        List<CarServiceEntity> cars =
                repo.findByVehicleNameIgnoreCaseAndDeletedFalse(brand);

        if (cars.isEmpty()) {
            throw new CarNotFoundException(
                    "No cars found with Brand - " + brand
            );
        }
        return cars;
    }

    /* ===================== GET CAR BY ID ===================== */
    public CarServiceEntity getCarById(Integer id) {
        return repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new CarNotFoundException(
                                "Car not found with ID - " + id
                        )
                );
    }

    /* ===================== SOFT DELETE ===================== */
    public String removeCarById(Integer id) {

        CarServiceEntity car = repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new CarNotFoundException(
                                "Car not found with ID - " + id
                        )
                );

        car.setDeleted(true);
        car.setStatus(CarStatus.INACTIVE);
        repo.save(car);

        return "Car Removed Successfully";
    }

    /* ===================== BOOKING: LOCK CAR ===================== */
    public void markCarAsBooked(Integer carId) {

        CarServiceEntity car = getCarById(carId);

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

        CarServiceEntity car = getCarById(carId);
        car.setStatus(CarStatus.AVAILABLE);
        repo.save(car);
    }
}
