package com.notification.NotificationService.Service;

import com.notification.NotificationService.DTO.NotificationEventDTO;
import com.notification.NotificationService.Entity.EmailLog;
import com.notification.NotificationService.Exception.EmailSendException;
import com.notification.NotificationService.Repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public NotificationService(NotificationRepository notificationRepository,
                               JavaMailSender javaMailSender) {
        this.notificationRepository = notificationRepository;
        this.javaMailSender = javaMailSender;
    }

    public String sendEmail(String to, String subject, String body) {

        EmailLog emailLog = new EmailLog();
        emailLog.setRecipientEmail(to);
        emailLog.setSubject(subject);
        emailLog.setBody(body);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(fromEmail);

            helper.setText(body, true);

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

    public String handleEvent(NotificationEventDTO request) {

        String subject = "";
        String body = "";

        String name = request.getName() != null ? request.getName() : "Customer";

        // 🔥 COMMON WRAPPER (AMAZON STYLE)
        String header = "<div style='background:#1a73e8;color:white;padding:20px;text-align:center;'>" +
                "<h2>🚗 Car Rental</h2></div>";

        String footer = "<div style='background:#f1f1f1;padding:15px;text-align:center;font-size:12px;color:#555;'>" +
                "<p>📧 support@carrental.com | 📞 +91-7801035298</p>" +
                "<p>© 2026 Car Rental. All rights reserved.</p></div>";

        String start = "<html><body style='font-family:Arial;background:#f4f4f4;padding:20px;'>" +
                "<div style='max-width:600px;margin:auto;background:#fff;border-radius:10px;overflow:hidden;'>"
                + header +
                "<div style='padding:20px;'>";

        String end = "</div>" + footer + "</div></body></html>";

        switch (request.getType()) {

            case USER_REGISTERED:
                subject = "🎉 Welcome to Car Rental";

                body = start +
                        "<h3>Welcome " + name + " 🚗</h3>" +
                        "<p>Your account has been successfully created.</p>" +
                        "<ul>" +
                        "<li>Browse cars</li>" +
                        "<li>Book instantly</li>" +
                        "<li>Enjoy seamless rides</li>" +
                        "</ul>" +
                        "<div style='text-align:center;margin-top:20px;'>" +
                        "<a href='#' style='background:#28a745;color:white;padding:10px 20px;border-radius:5px;text-decoration:none;'>Explore Cars</a>" +
                        "</div>" +
                        end;
                break;

            case OWNER_REGISTERED:
                subject = "🚘 Owner Account Activated";

                body = start +
                        "<h3>Welcome Partner " + name + "</h3>" +
                        "<p>Your owner account is successfully created.</p>" +
                        "<ul>" +
                        "<li>Add cars</li>" +
                        "<li>Manage bookings</li>" +
                        "<li>Earn revenue</li>" +
                        "</ul>" +
                        "<div style='text-align:center;margin-top:20px;'>" +
                        "<a href='#' style='background:#007bff;color:white;padding:10px 20px;border-radius:5px;text-decoration:none;'>Go to Dashboard</a>" +
                        "</div>" +
                        end;
                break;

            case CAR_ADDED:
                subject = "🚘 Car Added Successfully";

                body = start +
                        "<h3>Car Added</h3>" +
                        "<p>Your car <b>" + request.getCarName() + "</b> has been successfully listed.</p>" +
                        end;
                break;

            case BOOKING_CONFIRMED:
                subject = "🚗 Booking Confirmed";

                body = start +

                        "<h3 style='color:green;'>Booking Confirmed ✅</h3>" +
                        "<p>Hi <b>" + name + "</b>, your booking is confirmed.</p>" +

                        "<table style='width:100%;border-collapse:collapse;margin-top:15px;'>" +
                        "<tr style='background:#f1f1f1;'><td style='padding:10px;'>Booking ID</td><td>" + request.getBookingId() + "</td></tr>" +
                        "<tr><td style='padding:10px;'>Car</td><td>" + request.getCarName() + "</td></tr>" +
                        "<tr style='background:#f1f1f1;'><td style='padding:10px;'>Pickup</td><td>" + request.getPickupDate() + "</td></tr>" +
                        "<tr><td style='padding:10px;'>Return</td><td>" + request.getReturnDate() + "</td></tr>" +
                        "<tr style='background:#f1f1f1;'><td style='padding:10px;'>Location</td><td>" + request.getLocation() + "</td></tr>" +
                        "<tr><td style='padding:10px;'>Amount</td><td><b>₹" + request.getPrice() + "</b></td></tr>" +
                        "</table>" +

                        "<div style='margin-top:15px;padding:10px;background:#fff3cd;border-left:4px solid #ffc107;'>" +
                        "<b>Important:</b><br>" +
                        "Carry license, arrive early, follow rules." +
                        "</div>" +

                        "<div style='text-align:center;margin-top:20px;'>" +
                        "<a href='#' style='background:#28a745;color:white;padding:12px 20px;border-radius:5px;text-decoration:none;'>View Booking</a>" +
                        "</div>" +

                        end;
                break;

            case BOOKING_CANCELLED:
                subject = "❌ Booking Cancelled";

                body = start +
                        "<h3 style='color:red;'>Booking Cancelled</h3>" +
                        "<p>Your booking ID <b>" + request.getBookingId() + "</b> has been cancelled.</p>" +
                        "<p>If this wasn’t expected, contact support.</p>" +
                        end;
                break;

            default:
                throw new RuntimeException("Invalid Notification Type");
        }

        return sendEmail(request.getEmail(), subject, body);
    }
}