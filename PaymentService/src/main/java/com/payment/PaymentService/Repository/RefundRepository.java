package com.payment.PaymentService.Repository;

import com.payment.PaymentService.Entity.RefundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RefundRepository extends JpaRepository<RefundEntity, UUID> {

    List<RefundEntity> findByPaymentId(UUID paymentId);
}
