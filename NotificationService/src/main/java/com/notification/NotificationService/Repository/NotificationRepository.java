package com.notification.NotificationService.Repository;

import com.notification.NotificationService.Entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<EmailLog, Long> {

}
