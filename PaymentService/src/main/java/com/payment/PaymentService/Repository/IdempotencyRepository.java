package com.payment.PaymentService.Repository;

import com.payment.PaymentService.Entity.IdempotencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRepository
        extends JpaRepository<IdempotencyEntity, UUID> {

    Optional<IdempotencyEntity> findByIdempotencyKey(String idempotencyKey);
}
