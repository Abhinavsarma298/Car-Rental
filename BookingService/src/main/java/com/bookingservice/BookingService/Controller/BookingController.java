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

    // DELETE booking
    @DeleteMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id) {
        return service.deleteBooking(id);
    }
}
