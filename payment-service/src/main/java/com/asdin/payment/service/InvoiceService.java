package com.asdin.payment.service;

import com.asdin.payment.domain.*;
import com.asdin.payment.dto.*;
import com.asdin.payment.repository.InvoiceRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;

/** Idempotent invoice creation and owner-authorized settlement. */
@Service
public class InvoiceService {
    private final InvoiceRepository invoices;

    public InvoiceService(InvoiceRepository invoices) {
        this.invoices = invoices;
    }

    @Transactional
    public InvoiceResponse create(InvoiceCreateRequest r) {
        return invoices.findByItemId(r.getItemId()).map(this::map)
                .orElseGet(() -> map(invoices.save(Invoice.builder().itemId(r.getItemId()).winnerId(r.getWinnerId())
                        .amount(r.getAmount()).status(PaymentStatus.PENDING).createdAt(Instant.now()).build())));
    }

    public InvoiceResponse get(Long id, Long userId, boolean admin) {
        Invoice i = find(id);
        if (!admin && !i.getWinnerId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invoice does not belong to user");
        return map(i);
    }

    @Transactional
    public InvoiceResponse pay(Long id, Long userId, boolean admin) {
        Invoice i = find(id);
        if (!admin && !i.getWinnerId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invoice does not belong to user");
        if (i.getStatus() == PaymentStatus.PENDING) {
            i.setStatus(PaymentStatus.PAID);
            i.setPaidAt(Instant.now());
        }
        return map(i);
    }

    private Invoice find(Long id) {
        return invoices.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    private InvoiceResponse map(Invoice i) {
        return InvoiceResponse.builder().id(i.getId()).itemId(i.getItemId()).winnerId(i.getWinnerId())
                .amount(i.getAmount()).status(i.getStatus()).createdAt(i.getCreatedAt()).paidAt(i.getPaidAt()).build();
    }
}
