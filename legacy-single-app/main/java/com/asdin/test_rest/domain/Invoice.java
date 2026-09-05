package com.asdin.test_rest.domain;

import com.asdin.test_rest.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/** Invoice created after a sold auction. */
@Entity
@Table(name = "invoices", uniqueConstraints = @UniqueConstraint(columnNames = "item_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Invoice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "item_id", nullable = false, unique = true)
    private Long itemId;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private PaymentStatus status;
    @Column(nullable = false)
    private Instant createdAt;
    private Instant paidAt;
}
