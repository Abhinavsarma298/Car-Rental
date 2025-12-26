package com.bookingservice.BookingService.Service;

import com.bookingservice.BookingService.Entity.*;
import com.bookingservice.BookingService.Exception.*;
import com.bookingservice.BookingService.FeignClient.CarServiceClient;
import com.bookingservice.BookingService.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository repository;

    @Autowired
    private CarServiceClient carServiceClient;

    // ================= CREATE =================
    public Booking createBooking(Booking booking) {

        validateBooking(booking);

        carServiceClient.markCarAsBooked(booking.getVehicleId());

        try {
            checkVehicleAvailability(booking);
            booking.setStatus(BookingStatus.CREATED);
            return repository.save(booking);

        } catch (RuntimeException ex) {
            carServiceClient.markCarAsAvailable(booking.getVehicleId());
            throw ex;
        }
    }

    // ================= READ =================
    public List<Booking> getAllBookings() {
        List<Booking> bookings = repository.findAll();
        if (bookings.isEmpty()) {
            throw new BookingNotFoundException("No bookings found");
        }
        return bookings;
    }

    public Booking getBookingById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new BookingNotFoundException("Booking not found with ID - " + id));
    }

    // ================= UPDATE =================
    public Booking updateBooking(Long id, Booking booking) {

        Booking existing = getBookingById(id);

        validateBooking(booking);
        checkVehicleAvailabilityForUpdate(id, booking);

        booking.setId(id);
        booking.setStatus(existing.getStatus());

        return repository.save(booking);
    }

    // ================= DELETE (HARD DELETE) =================
    public String deleteBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking.getStatus() == BookingStatus.ONGOING) {
            throw new InvalidBookingStatusException(
                    "Ongoing booking cannot be deleted");
        }

        repository.deleteById(id);

        carServiceClient.markCarAsAvailable(booking.getVehicleId());

        return "Booking deleted successfully";
    }

    // ================= STATUS ACTIONS =================
    public Booking confirmBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking.getStatus() != BookingStatus.CREATED)
            throw new InvalidBookingStatusException(
                    "Only CREATED bookings can be confirmed");

        booking.setStatus(BookingStatus.CONFIRMED);
        return repository.save(booking);
    }

    public Booking startBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking.getStatus() != BookingStatus.CONFIRMED)
            throw new InvalidBookingStatusException(
                    "Only CONFIRMED bookings can be started");

        booking.setStatus(BookingStatus.ONGOING);
        return repository.save(booking);
    }

    public Booking completeBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking.getStatus() != BookingStatus.ONGOING)
            throw new InvalidBookingStatusException(
                    "Only ONGOING bookings can be completed");

        booking.setStatus(BookingStatus.COMPLETED);
        repository.save(booking);

        carServiceClient.markCarAsAvailable(booking.getVehicleId());

        return booking;
    }

    public Booking cancelBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking.getStatus() == BookingStatus.COMPLETED)
            throw new InvalidBookingStatusException(
                    "Completed bookings cannot be cancelled");

        booking.setStatus(BookingStatus.CANCELLED);
        repository.save(booking);

        carServiceClient.markCarAsAvailable(booking.getVehicleId());

        return booking;
    }

    // ================= ADMIN STATUS UPDATE =================
    public Booking updateBookingStatus(Long id, String status) {

        Booking booking = getBookingById(id);

        try {
            BookingStatus newStatus = BookingStatus.valueOf(status.toUpperCase());
            booking.setStatus(newStatus);
            return repository.save(booking);

        } catch (IllegalArgumentException ex) {
            throw new InvalidBookingStatusException("Invalid booking status: " + status);
        }
    }

    // ================= FILTER APIS =================
    public List<Booking> getBookingsByUser(Long userId) {
        List<Booking> bookings = repository.findByUserId(userId);
        if (bookings.isEmpty())
            throw new BookingNotFoundException("No bookings for user " + userId);
        return bookings;
    }

    public List<Booking> getBookingsByVehicle(Long vehicleId) {
        List<Booking> bookings = repository.findByVehicleId(vehicleId);
        if (bookings.isEmpty())
            throw new BookingNotFoundException("No bookings for vehicle " + vehicleId);
        return bookings;
    }

    public List<Booking> getActiveBookings() {
        List<Booking> bookings = repository.findByStatusIn(
                List.of(BookingStatus.CONFIRMED, BookingStatus.ONGOING));

        if (bookings.isEmpty())
            throw new BookingNotFoundException("No active bookings");

        return bookings;
    }

    // ================= VALIDATIONS =================
    private void validateBooking(Booking booking) {

        if (booking.getUserId() == null || booking.getUserId() <= 0)
            throw new InvalidBookingException("Invalid User ID");

        if (booking.getVehicleId() == null || booking.getVehicleId() <= 0)
            throw new InvalidBookingException("Invalid Vehicle ID");

        if (booking.getPickupDate() == null || booking.getPickupTime() == null ||
                booking.getDropoffDate() == null || booking.getDropoffTime() == null)
            throw new InvalidBookingException("Pickup & Dropoff date/time required");

        LocalDateTime pickup =
                LocalDateTime.of(booking.getPickupDate(), booking.getPickupTime());
        LocalDateTime dropoff =
                LocalDateTime.of(booking.getDropoffDate(), booking.getDropoffTime());

        if (!dropoff.isAfter(pickup))
            throw new InvalidBookingException("Dropoff must be after pickup");

        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(22, 0);

        if (booking.getPickupTime().isBefore(start) ||
                booking.getPickupTime().isAfter(end))
            throw new InvalidBookingException("Pickup time must be between 08:00–22:00");
    }

    // ================= AVAILABILITY =================
    private void checkVehicleAvailability(Booking booking) {

        LocalDateTime pickup =
                LocalDateTime.of(booking.getPickupDate(), booking.getPickupTime());
        LocalDateTime dropoff =
                LocalDateTime.of(booking.getDropoffDate(), booking.getDropoffTime());

        List<Booking> activeBookings =
                repository.findByVehicleIdAndStatusIn(
                        booking.getVehicleId(),
                        List.of(BookingStatus.CONFIRMED, BookingStatus.ONGOING));

        for (Booking existing : activeBookings) {

            LocalDateTime existingPickup =
                    LocalDateTime.of(existing.getPickupDate(), existing.getPickupTime());
            LocalDateTime existingDropoff =
                    LocalDateTime.of(existing.getDropoffDate(), existing.getDropoffTime());

            if (pickup.isBefore(existingDropoff) &&
                    dropoff.isAfter(existingPickup)) {
                throw new VehicleAlreadyBookedException(
                        "Vehicle already booked for this time slot");
            }
        }
    }

    private void checkVehicleAvailabilityForUpdate(Long bookingId, Booking booking) {
        checkVehicleAvailability(booking);
    }
}
