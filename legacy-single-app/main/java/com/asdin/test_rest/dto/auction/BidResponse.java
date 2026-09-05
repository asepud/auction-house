package com.asdin.test_rest.dto.auction;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/** Bid history projection. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BidResponse {
    private Long id;
    private Long bidderId;
    private String bidderName;
    private BigDecimal amount;
    private Instant createdAt;
}
