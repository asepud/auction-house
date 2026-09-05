package com.asdin.test_rest.dto.report;

import lombok.*;
import java.math.BigDecimal;

/** Native-query aggregate for a seller. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerSummary {
    private Long sellerId;
    private Long soldItems;
    private BigDecimal grossSales;
}
