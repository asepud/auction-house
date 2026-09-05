package com.asdin.test_rest.controller;

import com.asdin.test_rest.dto.common.NotificationResponse;
import com.asdin.test_rest.exception.BusinessException;
import com.asdin.test_rest.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/** Authenticated notification history. */
@RestController @RequestMapping("/api/notify")
public class NotificationController {
 private final NotificationService notifications; public NotificationController(NotificationService notifications){this.notifications=notifications;}
 @GetMapping("/history/{userId}") public List<NotificationResponse> history(@PathVariable Long userId,Authentication a){boolean admin=a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN"));if(!admin&&!Long.valueOf(a.getName()).equals(userId))throw new BusinessException(HttpStatus.FORBIDDEN,"Only the notification owner may view history");return notifications.history(userId);}
}
