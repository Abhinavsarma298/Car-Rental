package com.carservice.CarService.Service;

import com.carservice.CarService.CarRepo.CarRepo;
import com.carservice.CarService.Entity.CarServiceEntity;
import com.carservice.CarService.Exception.CarAlreadyExistsException;
import com.carservice.CarService.Exception.CarNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    @Autowired
    private CarRepo repo;

    // ✔ Register new car
    public CarServiceEntity register(CarServiceEntity car) {
        Optional<CarServiceEntity> existingCar =
                repo.findByRegistrationNumber(car.getRegistrationNumber());

        if (existingCar.isPresent()) {
            throw new CarAlreadyExistsException(
                    "Car already exists with Registration Number - " + car.getRegistrationNumber()
            );
        }

        return repo.save(car);
    }

    // ✔ Get all cars by owner id
    public List<CarServiceEntity> getCarsByOwnerId(Integer ownerId) {
        List<CarServiceEntity> cars = repo.findByOwnerId(ownerId);

        if (cars.isEmpty()) {
            throw new CarNotFoundException(
                    "No cars found for Owner ID - " + ownerId
            );
        }
        return cars;
    }

    // ✔ Get all cars by brand (vehicleName)
    public List<CarServiceEntity> getCarsByBrand(String brand) {
        List<CarServiceEntity> cars = repo.findByVehicleNameIgnoreCase(brand);
        if (cars.isEmpty()) {
            throw new CarNotFoundException(
                    "No cars found with Brand/Vehicle Name - " + brand
            );
        }
        return cars;
    }

    // ✔ Get single car by id
    public CarServiceEntity getCarById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new CarNotFoundException("Car not found with ID - " + id));
    }

    public String removeCarById(Integer id) {

        Optional<CarServiceEntity> exists = repo.findById(id);

        if(exists.isEmpty()){
            throw new CarNotFoundException("Car not found with ID - " + id);
        }

        CarServiceEntity car = exists.get();
        repo.delete(car);

        return "Car Removed Successfully";


    }
}
