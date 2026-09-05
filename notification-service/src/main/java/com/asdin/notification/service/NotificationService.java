package com.asdin.notification.service;

import com.asdin.notification.*;
import com.asdin.notification.domain.Notification;
import com.asdin.notification.dto.*;
import com.asdin.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

/** Persists externally delivered events and exposes owner history. */
@Service
public class NotificationService {
    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public void create(NotificationType type, NotificationRequest r) {
        repo.save(Notification.builder().userId(r.getUserId()).type(type).message(r.getMessage())
                .createdAt(Instant.now()).build());
    }

    public List<NotificationResponse> history(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> NotificationResponse.builder().id(n.getId()).userId(n.getUserId()).type(n.getType())
                        .message(n.getMessage()).createdAt(n.getCreatedAt()).build())
                .toList();
    }
}
