package com.payment.PaymentService.Repository;

import com.payment.PaymentService.Entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, UUID> {

    Optional<InvoiceEntity> findByPaymentId(UUID paymentId);
}
