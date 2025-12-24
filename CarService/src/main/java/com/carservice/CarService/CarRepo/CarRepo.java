package com.carservice.CarService.CarRepo;

import com.carservice.CarService.Entity.CarServiceEntity;
import com.carservice.CarService.Entity.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarRepo extends JpaRepository<CarServiceEntity, Integer> {

    /* ================= DUPLICATE CHECK ================= */
    Optional<CarServiceEntity> findByRegistrationNumber(String registrationNumber);

    /* ================= SOFT DELETE SAFE ================= */
    Optional<CarServiceEntity> findByIdAndDeletedFalse(Integer id);

    /* ================= OWNER QUERIES ================= */
    List<CarServiceEntity> findByOwnerIdAndDeletedFalse(Integer ownerId);

    /* ================= BRAND / VEHICLE NAME ================= */
    List<CarServiceEntity> findByVehicleNameIgnoreCaseAndDeletedFalse(String vehicleName);

    /* ================= BOOKING ================= */
    List<CarServiceEntity> findByStatusAndDeletedFalse(CarStatus status);
}
