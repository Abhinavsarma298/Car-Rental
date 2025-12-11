package com.bookingservice.BookingService.Service;

import com.bookingservice.BookingService.dto.BookingRequest;
import com.bookingservice.BookingService.dto.BookingResponse;

import java.math.BigDecimal;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
    BookingResponse getBooking(Long bookingId);
    List<BookingResponse> getBookingsForUser(Long userId);
    BookingResponse modifyBooking(Long bookingId, BookingRequest request);
    BookingResponse cancelBooking(Long bookingId, Long userId);
    BookingResponse confirmBooking(Long bookingId);
    BookingResponse startRental(Long bookingId);
    BookingResponse completeRental(Long bookingId);
    boolean checkAvailability(Long vehicleId, java.time.LocalDateTime pickup, java.time.LocalDateTime dropoff);
    BigDecimal estimateFare(Long vehicleId, java.time.LocalDateTime pickup, java.time.LocalDateTime dropoff, BigDecimal distanceKm);
    // placeholder for digital signature step
    BookingResponse signRentalAgreement(Long bookingId, Long userId, String signatureData);
}
