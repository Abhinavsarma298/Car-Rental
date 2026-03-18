package com.notification.NotificationService.DTO;

import com.notification.NotificationService.Enum.NotificationType;
import lombok.Data;

@Data
public class NotificationEventDTO {

    private String email;
    private String name;
    private NotificationType type;

    // Optional data
    private String carName;
    private String bookingId;
}