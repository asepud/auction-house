package com.asdin.test_rest.repository;

import com.asdin.test_rest.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
