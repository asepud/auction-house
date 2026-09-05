package com.asdin.payment.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/** Internal request received from auction-service at auction closure. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCreateRequest {
    @NotNull
    private Long itemId;
    @NotNull
    private Long winnerId;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
}
