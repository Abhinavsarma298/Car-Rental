package com.bookingservice.BookingService.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "CARSERVICE")
public interface CarServiceClient {

    @PutMapping("/api/cars/{carId}/book")
    void markCarAsBooked(@PathVariable("carId") Long carId);

    @PutMapping("/api/cars/{carId}/release")
    void markCarAsAvailable(@PathVariable("carId") Long carId);
}
