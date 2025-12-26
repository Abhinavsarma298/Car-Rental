package com.bookingservice.BookingService.FeignClient;

import com.bookingservice.BookingService.DTO.UserServerEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USERSERVICE")
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}")
    UserServerEntity getUserById(@PathVariable("userId") Long userId);
}
