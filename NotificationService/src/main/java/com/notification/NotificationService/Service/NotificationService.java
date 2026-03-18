package com.notification.NotificationService.Service;

import com.notification.NotificationService.DTO.NotificationEventDTO;
import com.notification.NotificationService.Entity.EmailLog;
import com.notification.NotificationService.Enum.NotificationType;
import com.notification.NotificationService.Exception.EmailSendException;
import com.notification.NotificationService.Repository.NotificationRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender javaMailSender;

    // ✅ Constructor Injection
    public NotificationService(NotificationRepository notificationRepository,
                               JavaMailSender javaMailSender) {
        this.notificationRepository = notificationRepository;
        this.javaMailSender = javaMailSender;
    }

    // 🔹 CORE EMAIL SENDER METHOD
    public String sendEmail(String to, String subject, String body) {

        EmailLog emailLog = new EmailLog();
        emailLog.setRecipientEmail(to);
        emailLog.setSubject(subject);
        emailLog.setBody(body);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("your-email@gmail.com"); // change this

            javaMailSender.send(message);

            emailLog.setStatus("SENT");

        } catch (Exception ex) {
            emailLog.setStatus("FAILED");
            notificationRepository.save(emailLog);
            throw new EmailSendException("Failed to send email: " + ex.getMessage());
        }

        notificationRepository.save(emailLog);

        return "Email Sent Successfully";
    }

    // 🔥 MAIN EVENT HANDLER
    public String handleEvent(NotificationEventDTO request) {

        String subject = "";
        String body = "";

        switch (request.getType()) {

            case USER_REGISTERED:
                subject = "Welcome to Car Rental 🚗";
                body = "Hi " + request.getName() +
                        ",\n\nYour account has been created successfully.\n\nHappy Renting!";
                break;

            case OWNER_REGISTERED:
                subject = "Owner Account Created ✅";
                body = "Hi " + request.getName() +
                        ",\n\nYour owner account is successfully created.\n\nYou can now add cars.";
                break;

            case CAR_ADDED:
                subject = "Car Added Successfully 🚘";
                body = "Your car \"" + request.getCarName() +
                        "\" has been added successfully.";
                break;

            case BOOKING_CONFIRMED:
                subject = "Booking Confirmed ✅";
                body = "Your booking (ID: " + request.getBookingId() +
                        ") has been confirmed.\n\nEnjoy your ride!";
                break;

            case BOOKING_CANCELLED:
                subject = "Booking Cancelled ❌";
                body = "Your booking (ID: " + request.getBookingId() +
                        ") has been cancelled.\n\nIf this was not intended, contact support.";
                break;

            default:
                throw new RuntimeException("Invalid Notification Type");
        }

        return sendEmail(request.getEmail(), subject, body);
    }
}