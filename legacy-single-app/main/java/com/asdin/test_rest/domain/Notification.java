package com.asdin.test_rest.domain;

import com.asdin.test_rest.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Auditable in-app notification. */
@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private NotificationType type;
    @Column(nullable = false, length = 500)
    private String message;
    @Column(nullable = false)
    private Instant createdAt;
}
