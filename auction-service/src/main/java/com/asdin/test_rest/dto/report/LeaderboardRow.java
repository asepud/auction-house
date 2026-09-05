package com.asdin.test_rest.dto.report;

import lombok.*;
import java.math.BigDecimal;

/** Native-query result for bidder ranking. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardRow {
    private Long bidderId;
    private String bidderName;
    private Long bidCount;
    private BigDecimal highestBid;
    private Integer ranking;
}
