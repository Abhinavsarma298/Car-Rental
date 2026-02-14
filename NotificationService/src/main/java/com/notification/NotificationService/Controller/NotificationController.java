package com.notification.NotificationService.Controller;

import com.notification.NotificationService.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Send Welcome Email
    @PostMapping("/welcome")
    public void sendWelcomeEmail(@RequestParam String email, @RequestParam String name) {
        String subject = "Welcome to Car Rental Application";
        String body = String.format("Hi %s,\n\nWelcome to Car Rental Application! We're excited to have you on board.\n\nBest regards,\nThe Admin Team", name);
        NotificationService.sendEmail(email, subject, body);
    }
}
