package com.asdin.test_rest.dto.auction;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/** A bid proposal. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidRequest {
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}
