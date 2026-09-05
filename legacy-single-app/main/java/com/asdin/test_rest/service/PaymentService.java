package com.asdin.test_rest.service;

import com.asdin.test_rest.dto.payment.InvoiceResponse;
import java.math.BigDecimal;

/** Payment simulation contract. */
public interface PaymentService {
    InvoiceResponse createInvoice(Long itemId, Long winnerId, BigDecimal amount);

    InvoiceResponse get(Long id, Long userId, boolean admin);

    InvoiceResponse pay(Long id, Long userId, boolean admin);
}
