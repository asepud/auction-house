package com.asdin.payment.controller;

import com.asdin.payment.dto.*;
import com.asdin.payment.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Public invoice lookup/payment plus internal, key-protected invoice creation.
 */
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final InvoiceService service;
    private final String key;

    public InvoiceController(InvoiceService service, @Value("${services.internal-key}") String key) {
        this.service = service;
        this.key = key;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceCreateRequest r,
            @RequestHeader(value = "X-Internal-Key", required = false) String header) {
        internal(header);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @GetMapping("/{id}")
    public InvoiceResponse get(@PathVariable Long id, Authentication a) {
        return service.get(id, uid(a), admin(a));
    }

    @PatchMapping("/{id}/pay")
    public InvoiceResponse pay(@PathVariable Long id, Authentication a) {
        return service.pay(id, uid(a), admin(a));
    }

    private void internal(String header) {
        if (!key.equals(header))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid internal API key");
    }

    private Long uid(Authentication a) {
        if (a == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return Long.valueOf(a.getName());
    }

    private boolean admin(Authentication a) {
        return a != null && a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"));
    }
}
