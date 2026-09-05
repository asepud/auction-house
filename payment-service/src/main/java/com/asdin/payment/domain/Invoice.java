package com.asdin.payment.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Payment database aggregate; it stores remote auction/user IDs, not foreign
 * keys.
 */
@Entity
@Table(name = "invoices", uniqueConstraints = @UniqueConstraint(columnNames = "itemId"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private Long itemId;
    @Column(nullable = false)
    private Long winnerId;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    @Column(nullable = false)
    private Instant createdAt;
    private Instant paidAt;
}
