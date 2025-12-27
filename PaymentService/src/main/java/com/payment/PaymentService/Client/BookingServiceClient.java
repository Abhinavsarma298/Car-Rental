package com.payment.PaymentService.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@FeignClient(name = "BOOKINGSERVICE")
public interface BookingServiceClient {

    @PutMapping("/bookings/{bookingId}/mark-paid")
    void markBookingAsPaid(@PathVariable UUID bookingId);

    @PutMapping("/bookings/{bookingId}/mark-cancelled")
    void markBookingAsCancelled(@PathVariable UUID bookingId);
}
