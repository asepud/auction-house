package com.asdin.test_rest.repository;

import com.asdin.test_rest.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByItemId(Long itemId);
}
