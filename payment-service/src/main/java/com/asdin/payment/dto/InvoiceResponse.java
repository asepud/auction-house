package com.asdin.payment.dto;

import com.asdin.payment.domain.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/** Payment service representation of an invoice. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {
    private Long id;
    private Long itemId;
    private Long winnerId;
    private BigDecimal amount;
    private PaymentStatus status;
    private Instant createdAt;
    private Instant paidAt;
}
