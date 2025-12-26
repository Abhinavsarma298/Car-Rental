package com.bookingservice.BookingService.Controller;

import com.bookingservice.BookingService.Entity.Booking;
import com.bookingservice.BookingService.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService service;

    // ================= CRUD =================

    // CREATE booking
    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        Booking savedBooking = service.createBooking(booking);
        return ResponseEntity.status(201).body(savedBooking);
    }

    // READ - get all bookings
    @GetMapping("/all")
    public List<Booking> getAllBookings() {
        return service.getAllBookings();
    }

    // READ - get booking by ID
    @GetMapping("/id/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return service.getBookingById(id);
    }

    // UPDATE booking
    @PutMapping("/update/{id}")
    public Booking updateBooking(
            @PathVariable Long id,
            @RequestBody Booking booking
    ) {
        return service.updateBooking(id, booking);
    }

    // DELETE booking (Admin / Hard delete)
    @DeleteMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id) {
        return service.deleteBooking(id);
    }

    // ================= REAL-WORLD ENDPOINTS =================

    // 1️⃣ Confirm booking
    @PutMapping("/{id}/confirm")
    public Booking confirmBooking(@PathVariable Long id) {
        return service.confirmBooking(id);
    }

    // 2️⃣ Start booking (pickup)
    @PutMapping("/{id}/start")
    public Booking startBooking(@PathVariable Long id) {
        return service.startBooking(id);
    }

    // 3️⃣ Complete booking (return)
    @PutMapping("/{id}/complete")
    public Booking completeBooking(@PathVariable Long id) {
        return service.completeBooking(id);
    }

    // 4️⃣ Cancel booking
    @PutMapping("/{id}/cancel")
    public Booking cancelBooking(@PathVariable Long id) {
        return service.cancelBooking(id);
    }

    // 5️⃣ Update booking status (Admin)
    @PatchMapping("/{id}/status")
    public Booking updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return service.updateBookingStatus(id, status);
    }

    // 6️⃣ Get bookings by user
    @GetMapping("/user/{userId}")
    public List<Booking> getBookingsByUser(@PathVariable Long userId) {
        return service.getBookingsByUser(userId);
    }

    // 7️⃣ Get bookings by vehicle
    @GetMapping("/vehicle/{vehicleId}")
    public List<Booking> getBookingsByVehicle(@PathVariable Long vehicleId) {
        return service.getBookingsByVehicle(vehicleId);
    }

    // 8️⃣ Get active bookings
    @GetMapping("/active")
    public List<Booking> getActiveBookings() {
        return service.getActiveBookings();
    }
}
