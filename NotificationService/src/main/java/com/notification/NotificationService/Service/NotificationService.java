package com.notification.NotificationService.Service;

import com.notification.NotificationService.Entity.EmailLog;
import com.notification.NotificationService.Exception.EmailSendException;
import com.notification.NotificationService.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private static NotificationRepository notificationRepository;


    @Autowired
    private static JavaMailSender javaMailSender;

    public static void sendEmail(String to, String subject, String body) {
        EmailLog emailLog = new EmailLog();
        emailLog.setRecipientEmail(to);
        emailLog.setSubject(subject);
        emailLog.setBody(body);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("21711a0563@necn.ac.in");

            javaMailSender.send(message);
            emailLog.setStatus("SENT");

        } catch (Exception ex) {
            emailLog.setStatus("FAILED");
            throw new EmailSendException("Failed to send email to " + to);
        }

        notificationRepository.save(emailLog);
    }
}
