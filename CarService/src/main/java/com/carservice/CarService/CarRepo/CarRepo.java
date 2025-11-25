package com.carservice.CarService.CarRepo;

import com.carservice.CarService.Entity.CarServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepo extends JpaRepository<CarServiceEntity, Integer> {
    // Get all cars by owner id
    List<CarServiceEntity> findByOwnerId(Integer ownerId);

    // Get all cars by car brand (assuming brand stored in vehicleName)
    List<CarServiceEntity> findByVehicleNameIgnoreCase(String vehicleName);

    Optional<CarServiceEntity> findByRegistrationNumber(String registrationNumber);
}
