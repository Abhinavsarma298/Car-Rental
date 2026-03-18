package com.notification.NotificationService.Controller;

import com.notification.NotificationService.DTO.NotificationEventDTO;
import com.notification.NotificationService.Service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // ✅ Constructor Injection
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/welcome")
    public String sendWelcomeEmail(
            @RequestParam String email,
            @RequestParam String name) {

        String subject = "Welcome to Car Rental Application";

        String body = "Hi " + name +
                ",\n\nWelcome to Car Rental Application! We're excited to have you onboard.";

        return notificationService.sendEmail(email, subject, body);
    }

    @PostMapping("/event")
    public String sendEvent(@RequestBody NotificationEventDTO request) {
        return notificationService.handleEvent(request);
    }
}