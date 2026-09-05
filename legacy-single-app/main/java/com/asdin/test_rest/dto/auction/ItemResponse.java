package com.asdin.test_rest.dto.auction;

import com.asdin.test_rest.enums.AuctionStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Public listing representation, optionally with bid history. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemResponse {
    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long sellerId;
    private String sellerName;
    private BigDecimal startingPrice;
    private BigDecimal currentHighestBid;
    private Instant startTime;
    private Instant endTime;
    private AuctionStatus status;
    private String imageUrl;
    private Long winnerId;
    private List<BidResponse> bids;
}
