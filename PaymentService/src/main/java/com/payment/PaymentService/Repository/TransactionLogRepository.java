package com.payment.PaymentService.Repository;

import com.payment.PaymentService.Entity.TransactionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionLogRepository
        extends JpaRepository<TransactionLogEntity, UUID> {

    List<TransactionLogEntity> findByPaymentId(UUID paymentId);
}
