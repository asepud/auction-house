package com.asdin.payment.repository;

import com.asdin.payment.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Persistence boundary for payment database. */
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByItemId(Long itemId);
}
