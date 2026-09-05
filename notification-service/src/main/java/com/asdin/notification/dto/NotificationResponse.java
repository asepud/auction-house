package com.asdin.notification.dto;

import com.asdin.notification.NotificationType;
import lombok.*;
import java.time.Instant;

/** History payload returned to the notification owner. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String message;
    private Instant createdAt;
}
