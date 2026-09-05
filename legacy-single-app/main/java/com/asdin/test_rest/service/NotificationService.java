package com.asdin.test_rest.service;

import com.asdin.test_rest.dto.common.NotificationResponse;
import com.asdin.test_rest.enums.NotificationType;
import java.util.*;

/**
 * Notification persistence contract; replaceable with an HTTP/event adapter.
 */
public interface NotificationService {
    void send(Long userId, NotificationType type, String message);

    List<NotificationResponse> history(Long userId);
}
