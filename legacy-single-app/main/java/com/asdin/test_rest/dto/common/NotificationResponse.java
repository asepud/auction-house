package com.asdin.test_rest.dto.common;

import com.asdin.test_rest.enums.NotificationType;
import lombok.*;
import java.time.Instant;

/** Notification history view. */
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
