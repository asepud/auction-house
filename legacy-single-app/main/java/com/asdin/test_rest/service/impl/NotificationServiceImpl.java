package com.asdin.test_rest.service.impl;

import com.asdin.test_rest.domain.Notification;
import com.asdin.test_rest.dto.common.NotificationResponse;
import com.asdin.test_rest.enums.NotificationType;
import com.asdin.test_rest.exception.BusinessException;
import com.asdin.test_rest.repository.*;
import com.asdin.test_rest.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

/** Database notification adapter for the first runnable release. */
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notifications;
    private final UserRepository users;

    public NotificationServiceImpl(NotificationRepository notifications, UserRepository users) {
        this.notifications = notifications;
        this.users = users;
    }

    public void send(Long userId, NotificationType type, String message) {
        var user = users.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        notifications
                .save(Notification.builder().user(user).type(type).message(message).createdAt(Instant.now()).build());
    }

    public List<NotificationResponse> history(Long userId) {
        return notifications.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> NotificationResponse.builder().id(n.getId()).userId(n.getUser().getId()).type(n.getType())
                        .message(n.getMessage()).createdAt(n.getCreatedAt()).build())
                .toList();
    }
}
