package com.asdin.notification.domain;

import com.asdin.notification.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Notification service aggregate, using the remote user ID as scalar data. */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    @Column(nullable = false, length = 500)
    private String message;
    @Column(nullable = false)
    private Instant createdAt;
}
