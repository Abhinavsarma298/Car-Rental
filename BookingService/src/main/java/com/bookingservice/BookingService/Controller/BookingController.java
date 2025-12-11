package com.bookingservice.BookingService.Controller;

import com.bookingservice.BookingService.Service.BookingService;
import com.bookingservice.BookingService.dto.BookingRequest;
import com.bookingservice.BookingService.dto.BookingResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Check real-time availability
    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam Long vehicleId,
            @RequestParam String pickup,   // ISO-8601 string
            @RequestParam String dropoff) {
        var pickupDt = java.time.LocalDateTime.parse(pickup);
        var dropoffDt = java.time.LocalDateTime.parse(dropoff);
        boolean available = bookingService.checkAvailability(vehicleId, pickupDt, dropoffDt);
        return ResponseEntity.ok(available);
    }

    // Fare estimate
    @GetMapping("/estimate")
    public ResponseEntity<BigDecimal> estimateFare(
            @RequestParam Long vehicleId,
            @RequestParam String pickup,
            @RequestParam String dropoff,
            @RequestParam(required = false) BigDecimal distanceKm) {
        var pickupDt = java.time.LocalDateTime.parse(pickup);
        var dropoffDt = java.time.LocalDateTime.parse(dropoff);
        BigDecimal estimate = bookingService.estimateFare(vehicleId, pickupDt, dropoffDt, distanceKm);
        return ResponseEntity.ok(estimate);
    }

    // Create booking (instant request)
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.ok(response);
    }

    // Get single booking
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }

    // Get booking history for user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(userId));
    }

    // Modify booking
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> modifyBooking(@PathVariable Long id, @Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.modifyBooking(id, request));
    }

    // Cancel booking
    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, userId));
    }

    // Confirm booking (e.g. after payment or signature)
    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.confirmBooking(id));
    }

    // Start rental (vehicle picked up)
    @PostMapping("/{id}/start")
    public ResponseEntity<BookingResponse> startRental(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.startRental(id));
    }

    // Complete rental (vehicle returned)
    @PostMapping("/{id}/complete")
    public ResponseEntity<BookingResponse> completeRental(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.completeRental(id));
    }

    // Sign rental agreement (digital signing)
    @PostMapping("/{id}/sign")
    public ResponseEntity<BookingResponse> signAgreement(@PathVariable Long id,
                                                         @RequestParam Long userId,
                                                         @RequestBody String signatureData) {
        return ResponseEntity.ok(bookingService.signRentalAgreement(id, userId, signatureData));
    }
}
