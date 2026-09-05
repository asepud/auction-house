package com.asdin.test_rest.domain;

import com.asdin.test_rest.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/** Aggregate root for a listing and its auction lifecycle. */
@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    @Column(nullable = false, length = 180)
    private String title;
    @Column(nullable = false, length = 3000)
    private String description;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal startingPrice;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentHighestBid;
    @Column(nullable = false)
    private Instant startTime;
    @Column(nullable = false)
    private Instant endTime;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuctionStatus status;
    @Column(length = 500)
    private String imageUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;
}
