package com.asdin.notification.repository;

import com.asdin.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

/** Persistence boundary for notification database. */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
