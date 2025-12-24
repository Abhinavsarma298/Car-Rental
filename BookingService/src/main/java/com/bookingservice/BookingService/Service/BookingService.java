package com.bookingservice.BookingService.Service;

import com.bookingservice.BookingService.Entity.Booking;
import com.bookingservice.BookingService.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository repository;

    // CREATE
    public Booking createBooking(Booking booking) {

        validateBooking(booking);

        return repository.save(booking);
    }

    // READ - all
    public List<Booking> getAllBookings() {
        return repository.findAll();
    }

    // READ - by ID
    public Booking getBookingById(Long id) {
        return repository.findById(id).orElse(null);
    }

    // UPDATE
    public Booking updateBooking(Long id, Booking booking) {

        Booking existing = repository.findById(id).orElse(null);

        if (existing == null) {
            throw new RuntimeException("Booking not found with id: " + id);
        }

        validateBooking(booking);

        booking.setId(id);
        return repository.save(booking);
    }

    // DELETE
    public String deleteBooking(Long id) {

        Booking existing = repository.findById(id).orElse(null);

        if (existing == null) {
            throw new RuntimeException("Booking not found with id: " + id);
        }

        repository.deleteById(id);
        return "Booking deleted successfully";
    }

    // ===================== VALIDATIONS =====================

    private void validateBooking(Booking booking) {

        // userId validation
        if (booking.getUserId() == null || booking.getUserId() <= 0) {
            throw new RuntimeException("User ID must be a positive number");
        }

        // vehicleId validation
        if (booking.getVehicleId() == null || booking.getVehicleId() <= 0) {
            throw new RuntimeException("Vehicle ID must be a positive number");
        }

        // pickup & dropoff validation
        LocalDateTime pickup = booking.getPickupDatetime();
        LocalDateTime dropoff = booking.getDropoffDatetime();

        if (pickup == null || dropoff == null) {
            throw new RuntimeException("Pickup and Dropoff datetime cannot be null");
        }

        if (dropoff.isBefore(pickup)) {
            throw new RuntimeException("Dropoff datetime must be after pickup datetime");
        }

        // status validation
        if (booking.getStatus() == null || booking.getStatus().trim().isEmpty()) {
            throw new RuntimeException("Booking status cannot be empty");
        }

        // amount validations
        if (booking.getTotalAmount() != null && booking.getTotalAmount().doubleValue() < 0) {
            throw new RuntimeException("Total amount cannot be negative");
        }

        if (booking.getSecurityDeposit() != null && booking.getSecurityDeposit().doubleValue() < 0) {
            throw new RuntimeException("Security deposit cannot be negative");
        }

        if (booking.getTotalDistance() != null && booking.getTotalDistance().doubleValue() < 0) {
            throw new RuntimeException("Total distance cannot be negative");
        }
    }
}
