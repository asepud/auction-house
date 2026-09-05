package com.asdin.test_rest.dto.payment;

import com.asdin.test_rest.enums.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/** Invoice view used by payment endpoints. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InvoiceResponse {
    private Long id;
    private Long itemId;
    private Long winnerId;
    private BigDecimal amount;
    private PaymentStatus status;
    private Instant createdAt;
    private Instant paidAt;
}
