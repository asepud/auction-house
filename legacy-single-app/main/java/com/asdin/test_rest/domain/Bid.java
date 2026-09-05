package com.asdin.test_rest.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/** Immutable offer placed by a bidder. */
@Entity
@Table(name = "bids", indexes = @Index(name = "idx_bids_item_amount", columnList = "item_id,amount"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bid {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private AuctionItem item;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id")
    private User bidder;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false)
    private Instant createdAt;
}
