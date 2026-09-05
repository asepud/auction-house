package com.asdin.notification.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/** Internal event payload emitted by auction-service. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    @NotNull
    private Long userId;
    @NotBlank
    @Size(max = 500)
    private String message;
}
