package com.payment.PaymentService.Service;

import com.payment.PaymentService.Client.BookingServiceClient;
import com.payment.PaymentService.Entity.*;
import com.payment.PaymentService.Exception.*;
import com.payment.PaymentService.Repository.*;
import com.payment.PaymentService.Util.WebhookSignatureUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final TransactionLogRepository transactionLogRepository;
    private final InvoiceRepository invoiceRepository;
    private final BookingServiceClient bookingServiceClient;
    private final IdempotencyRepository idempotencyRepository;


    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    // ------------------ INITIATE PAYMENT ------------------

    public PaymentEntity initiatePayment(
            String idempotencyKey,
            UUID bookingId,
            UUID userId,
            UUID ownerId
    ) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new InvalidPaymentException("Idempotency-Key is required");
        }

        // 1️⃣ Check existing key
        idempotencyRepository.findByIdempotencyKey(idempotencyKey)
                .ifPresent(existing -> {
                    PaymentEntity existingPayment = paymentRepository
                            .findById(existing.getResourceId())
                            .orElseThrow();
                    throw new PaymentAlreadyProcessedException(
                            "Duplicate request. Payment already created"
                    );
                });

        // 2️⃣ Normal validations
        if (bookingId == null || userId == null || ownerId == null) {
            throw new InvalidPaymentException("Invalid input");
        }

        if (paymentRepository.findByBookingId(bookingId).isPresent()) {
            throw new DuplicatePaymentException("Payment already exists for booking");
        }

        BigDecimal amount = BigDecimal.valueOf(1000);

        PaymentEntity payment = PaymentEntity.builder()
                .bookingId(bookingId)
                .userId(userId)
                .ownerId(ownerId)
                .amount(amount)
                .currency("INR")
                .paymentMethod(PaymentMethod.UPI)
                .gatewayName("Razorpay")
                .paymentStatus(PaymentStatus.INITIATED)
                .preAuthorization(false)
                .build();

        PaymentEntity savedPayment = paymentRepository.save(payment);

        // 3️⃣ Save idempotency key
        idempotencyRepository.save(
                IdempotencyEntity.builder()
                        .idempotencyKey(idempotencyKey)
                        .resourceId(savedPayment.getPaymentId())
                        .build()
        );

        return savedPayment;
    }

    // ------------------ VERIFY PAYMENT ------------------

    public PaymentEntity verifyPayment(String transactionId) {

        if (transactionId == null || transactionId.isBlank()) {
            throw new InvalidPaymentException("Transaction ID must not be empty");
        }

        PaymentEntity payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return payment;
        }

        if (payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new PaymentAlreadyProcessedException("Refunded payment cannot be verified");
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(transactionId);

        PaymentEntity savedPayment = paymentRepository.save(payment);

        transactionLogRepository.save(
                TransactionLogEntity.builder()
                        .paymentId(savedPayment.getPaymentId())
                        .transactionType(TransactionType.PAYMENT_SUCCESS)
                        .message("Payment verified successfully")
                        .build()
        );

        // ✅ CREATE INVOICE (IDEMPOTENT)
        if (invoiceRepository.findByPaymentId(savedPayment.getPaymentId()).isEmpty()) {
            InvoiceEntity invoice = InvoiceEntity.builder()
                    .paymentId(savedPayment.getPaymentId())
                    .invoiceNumber("INV-" + UUID.randomUUID())
                    .amount(savedPayment.getAmount())
                    .currency(savedPayment.getCurrency())
                    .build();

            invoiceRepository.save(invoice);
        }

        bookingServiceClient.markBookingAsPaid(payment.getBookingId());

        return savedPayment;
    }

    // ------------------ PRE-AUTH CAPTURE ------------------

    public PaymentEntity capturePreAuthorizedPayment(UUID paymentId) {

        if (paymentId == null) {
            throw new InvalidPaymentException("Payment ID must not be null");
        }

        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if (!payment.getPreAuthorization()) {
            throw new InvalidPaymentException("Payment is not pre-authorized");
        }

        if (payment.getPaymentStatus() != PaymentStatus.INITIATED) {
            throw new InvalidPaymentException("Only initiated payments can be captured");
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPreAuthorization(false);

        return paymentRepository.save(payment);
    }

    // ------------------ REFUND ------------------

    public void refundPayment(UUID paymentId, Double refundAmount) {

        if (paymentId == null) {
            throw new InvalidPaymentException("Payment ID must not be null");
        }

        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new InvalidPaymentException("Only successful payments can be refunded");
        }

        BigDecimal refundValue =
                refundAmount == null ? payment.getAmount() : BigDecimal.valueOf(refundAmount);

        if (refundValue.compareTo(payment.getAmount()) > 0) {
            throw new InvalidPaymentException("Refund amount cannot exceed paid amount");
        }

        RefundEntity refund = RefundEntity.builder()
                .paymentId(paymentId)
                .refundAmount(refundValue)
                .refundStatus(RefundStatus.SUCCESS)
                .reason("Booking cancelled")
                .build();

        refundRepository.save(refund);

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        transactionLogRepository.save(
                TransactionLogEntity.builder()
                        .paymentId(paymentId)
                        .transactionType(TransactionType.PAYMENT_REFUNDED)
                        .message("Payment refunded")
                        .build()
        );

        bookingServiceClient.markBookingAsCancelled(payment.getBookingId());
    }

    // ------------------ FETCH METHODS ------------------

    public PaymentEntity getPaymentById(UUID paymentId) {
        if (paymentId == null) {
            throw new InvalidPaymentException("Payment ID must not be null");
        }

        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
    }

    public PaymentEntity getPaymentByBookingId(UUID bookingId) {
        if (bookingId == null) {
            throw new InvalidPaymentException("Booking ID must not be null");
        }

        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for booking"));
    }

    public List<PaymentEntity> getPaymentsByUser(UUID userId) {
        if (userId == null) {
            throw new InvalidPaymentException("User ID must not be null");
        }
        return paymentRepository.findByUserId(userId);
    }

    public List<PaymentEntity> getPaymentsByOwner(UUID ownerId) {
        if (ownerId == null) {
            throw new InvalidPaymentException("Owner ID must not be null");
        }
        return paymentRepository.findByOwnerId(ownerId);
    }

    public List<PaymentEntity> getAllPayments() {
        return paymentRepository.findAll();
    }

    // ------------------ WEBHOOK ------------------

    public void handleWebhook(String payload, String signature) {

        if (payload == null || signature == null) {
            throw new InvalidPaymentException("Invalid webhook payload or signature");
        }

        boolean valid = WebhookSignatureUtil.verify(payload, signature, webhookSecret);
        if (!valid) {
            throw new InvalidPaymentException("Webhook signature verification failed");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);
            JsonNode paymentNode = root.path("payload").path("payment").path("entity");

            String transactionId = paymentNode.path("id").asText();
            String status = paymentNode.path("status").asText();

            PaymentEntity payment = paymentRepository
                    .findByTransactionId(transactionId)
                    .orElseThrow(() ->
                            new PaymentNotFoundException("Payment not found for webhook"));

            transactionLogRepository.save(
                    TransactionLogEntity.builder()
                            .paymentId(payment.getPaymentId())
                            .transactionType(TransactionType.WEBHOOK_RECEIVED)
                            .message("Webhook received: " + status)
                            .build()
            );

            if (payment.getPaymentStatus() == PaymentStatus.SUCCESS &&
                    "captured".equals(status)) {
                return;
            }

            if ("captured".equals(status)) {

                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);

                // ✅ CREATE INVOICE (WEBHOOK SAFE)
                if (invoiceRepository.findByPaymentId(payment.getPaymentId()).isEmpty()) {
                    InvoiceEntity invoice = InvoiceEntity.builder()
                            .paymentId(payment.getPaymentId())
                            .invoiceNumber("INV-" + UUID.randomUUID())
                            .amount(payment.getAmount())
                            .currency(payment.getCurrency())
                            .build();

                    invoiceRepository.save(invoice);
                }

                bookingServiceClient.markBookingAsPaid(payment.getBookingId());
            }

            if ("failed".equals(status)) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }

        } catch (Exception e) {
            throw new InvalidPaymentException("Failed to process webhook");
        }
    }
}
