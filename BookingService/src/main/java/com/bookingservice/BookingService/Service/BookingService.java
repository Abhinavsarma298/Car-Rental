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

        // 🔐 LOCK CAR (vehicleId EXISTS)
        carServiceClient.markCarAsBooked(booking.getVehicleId());

        try {
            checkVehicleAvailability(booking);

            booking.setStatus(BookingStatus.CREATED);
            return repository.save(booking);

        } catch (RuntimeException ex) {
            // ❗ rollback car lock if booking fails
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
                        new BookingNotFoundException(
                                "Booking not found with ID - " + id
                        )
                );
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

    // ================= DELETE =================
    public String deleteBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new InvalidBookingStatusException(
                    "Completed bookings cannot be cancelled"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);
        repository.save(booking);

        // 🔓 RELEASE CAR
        carServiceClient.markCarAsAvailable(booking.getVehicleId());

        return "Booking cancelled successfully";
    }

    // ================= STATUS ACTIONS =================

    public Booking confirmBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking.getStatus() != BookingStatus.CREATED) {
            throw new InvalidBookingStatusException(
                    "Only CREATED bookings can be confirmed"
            );
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        return repository.save(booking);
    }

    public Booking startBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidBookingStatusException(
                    "Only CONFIRMED bookings can be started"
            );
        }

        booking.setStatus(BookingStatus.ONGOING);
        return repository.save(booking);
    }

    public Booking completeBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking.getStatus() != BookingStatus.ONGOING) {
            throw new InvalidBookingStatusException(
                    "Only ONGOING bookings can be completed"
            );
        }

        booking.setStatus(BookingStatus.COMPLETED);
        repository.save(booking);

        // 🔓 RELEASE CAR AFTER COMPLETION
        carServiceClient.markCarAsAvailable(booking.getVehicleId());

        return booking;
    }

    public Booking cancelBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new InvalidBookingStatusException(
                    "Completed bookings cannot be cancelled"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);
        repository.save(booking);

        // 🔓 RELEASE CAR
        carServiceClient.markCarAsAvailable(booking.getVehicleId());

        return booking;
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

        if (pickup.isBefore(LocalDateTime.now()))
            throw new InvalidBookingException("Pickup time cannot be in the past");

        if (pickup.isBefore(LocalDateTime.now().plusMinutes(30)))
            throw new InvalidBookingException("Pickup must be at least 30 minutes from now");

        if (!dropoff.isAfter(pickup))
            throw new InvalidBookingException("Dropoff must be after pickup");

        if (pickup.plusHours(1).isAfter(dropoff))
            throw new InvalidBookingException("Minimum booking duration is 1 hour");

        if (pickup.plusDays(30).isBefore(dropoff))
            throw new InvalidBookingException("Maximum booking duration is 30 days");

        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(22, 0);

        if (booking.getPickupTime().isBefore(start) ||
                booking.getPickupTime().isAfter(end))
            throw new InvalidBookingException("Pickup time must be between 08:00–22:00");

        if (booking.getDropoffTime().isBefore(start) ||
                booking.getDropoffTime().isAfter(end))
            throw new InvalidBookingException("Dropoff time must be between 08:00–22:00");
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
                        List.of(
                                BookingStatus.CONFIRMED,
                                BookingStatus.ONGOING
                        )
                );

        for (Booking existing : activeBookings) {

            LocalDateTime existingPickup =
                    LocalDateTime.of(existing.getPickupDate(), existing.getPickupTime());
            LocalDateTime existingDropoff =
                    LocalDateTime.of(existing.getDropoffDate(), existing.getDropoffTime());

            if (pickup.isBefore(existingDropoff) &&
                    dropoff.isAfter(existingPickup)) {

                throw new VehicleAlreadyBookedException(
                        "Vehicle already booked for this time slot"
                );
            }
        }
    }

    private void checkVehicleAvailabilityForUpdate(
            Long bookingId, Booking booking) {

        List<Booking> activeBookings =
                repository.findByVehicleIdAndStatusIn(
                        booking.getVehicleId(),
                        List.of(
                                BookingStatus.CONFIRMED,
                                BookingStatus.ONGOING
                        )
                );

        for (Booking existing : activeBookings) {
            if (!existing.getId().equals(bookingId)) {
                checkVehicleAvailability(booking);
            }
        }
    }
}
