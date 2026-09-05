package com.asdin.test_rest.service.impl;

import com.asdin.test_rest.domain.Invoice;
import com.asdin.test_rest.dto.payment.InvoiceResponse;
import com.asdin.test_rest.enums.PaymentStatus;
import com.asdin.test_rest.exception.BusinessException;
import com.asdin.test_rest.repository.*;
import com.asdin.test_rest.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;

/** Idempotent local payment-service implementation. */
@Service
public class PaymentServiceImpl implements PaymentService {
    private final InvoiceRepository invoices;
    private final UserRepository users;

    public PaymentServiceImpl(InvoiceRepository invoices, UserRepository users) {
        this.invoices = invoices;
        this.users = users;
    }

    @Transactional
    public InvoiceResponse createInvoice(Long itemId, Long winnerId, BigDecimal amount) {
        return invoices.findByItemId(itemId).map(this::map).orElseGet(
                () -> map(invoices.save(Invoice.builder().itemId(itemId).winner(users.getReferenceById(winnerId))
                        .amount(amount).status(PaymentStatus.PENDING).createdAt(Instant.now()).build())));
    }

    public InvoiceResponse get(Long id, Long userId, boolean admin) {
        Invoice invoice = invoices.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Invoice not found"));
        if (!admin && !invoice.getWinner().getId().equals(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Only the invoice winner may view it");
        }
        return map(invoice);
    }

    @Transactional
    public InvoiceResponse pay(Long id, Long userId, boolean admin) {
        Invoice i = invoices.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Invoice not found"));
        if (!admin && !i.getWinner().getId().equals(userId))
            throw new BusinessException(HttpStatus.FORBIDDEN, "Only the invoice winner may pay");
        if (i.getStatus() == PaymentStatus.PENDING) {
            i.setStatus(PaymentStatus.PAID);
            i.setPaidAt(Instant.now());
        }
        return map(i);
    }

    private InvoiceResponse map(Invoice i) {
        return InvoiceResponse.builder().id(i.getId()).itemId(i.getItemId()).winnerId(i.getWinner().getId())
                .amount(i.getAmount()).status(i.getStatus()).createdAt(i.getCreatedAt()).paidAt(i.getPaidAt()).build();
    }
}
