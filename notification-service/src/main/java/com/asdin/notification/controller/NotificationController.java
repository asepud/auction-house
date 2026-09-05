package com.asdin.notification.controller;

import com.asdin.notification.*;
import com.asdin.notification.dto.*;
import com.asdin.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

/** Internal event receiver and authenticated history endpoint. */
@RestController
@RequestMapping("/api/notify")
public class NotificationController {
    private final NotificationService service;
    private final String key;

    public NotificationController(NotificationService service, @Value("${services.internal-key}") String key) {
        this.service = service;
        this.key = key;
    }

    @PostMapping("/outbid")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void outbid(@Valid @RequestBody NotificationRequest r,
            @RequestHeader(value = "X-Internal-Key", required = false) String h) {
        internal(h);
        service.create(NotificationType.OUTBID, r);
    }

    @PostMapping("/winner")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void winner(@Valid @RequestBody NotificationRequest r,
            @RequestHeader(value = "X-Internal-Key", required = false) String h) {
        internal(h);
        service.create(NotificationType.WINNER, r);
    }

    @PostMapping("/ended")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ended(@Valid @RequestBody NotificationRequest r,
            @RequestHeader(value = "X-Internal-Key", required = false) String h) {
        internal(h);
        service.create(NotificationType.AUCTION_ENDED, r);
    }

    @GetMapping("/history/{userId}")
    public List<NotificationResponse> history(@PathVariable Long userId, Authentication a) {
        boolean admin = a != null && a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"));
        if (a == null || (!admin && !Long.valueOf(a.getName()).equals(userId)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner or admin may view history");
        return service.history(userId);
    }

    private void internal(String h) {
        if (!key.equals(h))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid internal API key");
    }
}
