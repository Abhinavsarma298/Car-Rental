package com.bookingservice.BookingService.Repository;

import com.bookingservice.BookingService.Entity.Booking;
import com.bookingservice.BookingService.Entity.RentalState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByBookedAtDesc(Long userId);

    List<Booking> findByVehicleIdAndStatusInAndPickupDatetimeBetween(
            Long vehicleId, List<String> statuses, LocalDateTime start, LocalDateTime end);

    // Find bookings for a vehicle in a given time window (used for availability)
    List<Booking> findByVehicleIdAndPickupDatetimeLessThanEqualAndDropoffDatetimeGreaterThanEqual(
            Long vehicleId, LocalDateTime dropoff, LocalDateTime pickup);

    List<Booking> findByStatus(String status);

    // Optionally: find bookings overlapping a window for availability
    List<Booking> findByVehicleIdAndDropoffDatetimeGreaterThanEqualAndPickupDatetimeLessThanEqual(
            Long vehicleId, LocalDateTime pickup, LocalDateTime dropoff);
}
