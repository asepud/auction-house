package com.asdin.test_rest.controller;

import com.asdin.test_rest.dto.payment.InvoiceResponse;
import com.asdin.test_rest.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** Payment simulation endpoints; invoices are created by auction closure. */
@RestController @RequestMapping("/api/invoices")
public class PaymentController {
 private final PaymentService payments; public PaymentController(PaymentService payments){this.payments=payments;}
 @GetMapping("/{id}") public InvoiceResponse get(@PathVariable Long id,Authentication a){boolean admin=a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN"));return payments.get(id,Long.valueOf(a.getName()),admin);}
 @PatchMapping("/{id}/pay") public InvoiceResponse pay(@PathVariable Long id,Authentication a){boolean admin=a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN"));return payments.pay(id,Long.valueOf(a.getName()),admin);}
}
