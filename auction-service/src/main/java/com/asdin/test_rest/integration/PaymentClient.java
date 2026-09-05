package com.asdin.test_rest.integration;
import java.math.BigDecimal;
/** Outbound contract to the separately deployed payment service. */
public interface PaymentClient { void createInvoice(Long itemId, Long winnerId, BigDecimal amount); }
