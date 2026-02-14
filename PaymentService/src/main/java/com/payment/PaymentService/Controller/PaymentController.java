package com.payment.PaymentService.Controller;

import com.payment.PaymentService.Entity.*;
import com.payment.PaymentService.Repository.InvoiceRepository;
import com.payment.PaymentService.Repository.RefundRepository;
import com.payment.PaymentService.Repository.TransactionLogRepository;
import com.payment.PaymentService.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.payment.PaymentService.Util.InvoicePdfGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final InvoiceRepository invoiceRepository;
    private final RefundRepository refundRepository;
    private final TransactionLogRepository transactionLogRepository;

    // ------------------ PAYMENT INITIATION ------------------

    @PostMapping("/initiate")
    public ResponseEntity<PaymentEntity> initiatePayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestParam UUID bookingId,
            @RequestParam UUID userId,
            @RequestParam UUID ownerId
    ) {
        return ResponseEntity.ok(
                paymentService.initiatePayment(
                        idempotencyKey, bookingId, userId, ownerId
                )
        );
    }


    // ------------------ PAYMENT VERIFICATION ------------------

    @PostMapping("/verify")
    public ResponseEntity<PaymentEntity> verifyPayment(
            @RequestParam String transactionId
    ) {
        return ResponseEntity.ok(
                paymentService.verifyPayment(transactionId)
        );
    }

    // ------------------ PRE-AUTH CAPTURE ------------------

    @PostMapping("/capture")
    public ResponseEntity<PaymentEntity> capturePayment(
            @RequestParam UUID paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.capturePreAuthorizedPayment(paymentId)
        );
    }

    // ------------------ REFUND ------------------

    @PostMapping("/refund")
    public ResponseEntity<String> refundPayment(
            @RequestParam UUID paymentId,
            @RequestParam(required = false) Double amount
    ) {
        paymentService.refundPayment(paymentId, amount);
        return ResponseEntity.ok("Refund initiated successfully");
    }

    // ------------------ PAYMENT FETCH ------------------

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentEntity> getPaymentById(
            @PathVariable UUID paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.getPaymentById(paymentId)
        );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentEntity> getPaymentByBookingId(
            @PathVariable UUID bookingId
    ) {
        return ResponseEntity.ok(
                paymentService.getPaymentByBookingId(bookingId)
        );
    }

    // ------------------ PAYMENT HISTORY ------------------

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentEntity>> getPaymentsByUser(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                paymentService.getPaymentsByUser(userId)
        );
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PaymentEntity>> getPaymentsByOwner(
            @PathVariable UUID ownerId
    ) {
        return ResponseEntity.ok(
                paymentService.getPaymentsByOwner(ownerId)
        );
    }

    // ------------------ INVOICES (STEP 6) ------------------

    @GetMapping("/invoice/payment/{paymentId}")
    public ResponseEntity<InvoiceEntity> getInvoiceByPaymentId(
            @PathVariable UUID paymentId
    ) {
        return ResponseEntity.ok(
                invoiceRepository.findByPaymentId(paymentId)
                        .orElseThrow(() ->
                                new RuntimeException("Invoice not found for payment"))
        );
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<InvoiceEntity> getInvoiceByInvoiceId(
            @PathVariable UUID invoiceId
    ) {
        return ResponseEntity.ok(
                invoiceRepository.findById(invoiceId)
                        .orElseThrow(() ->
                                new RuntimeException("Invoice not found"))
        );
    }

    // ------------------ REFUNDS (STEP 4) ------------------

    @GetMapping("/refunds/payment/{paymentId}")
    public ResponseEntity<List<RefundEntity>> getRefundsByPaymentId(
            @PathVariable UUID paymentId
    ) {
        return ResponseEntity.ok(
                refundRepository.findByPaymentId(paymentId)
        );
    }

    // ------------------ TRANSACTION LOGS (STEP 5) ------------------

    @GetMapping("/transactions/payment/{paymentId}")
    public ResponseEntity<List<TransactionLogEntity>> getTransactionLogs(
            @PathVariable UUID paymentId
    ) {
        return ResponseEntity.ok(
                transactionLogRepository.findByPaymentId(paymentId)
        );
    }

    // ------------------ WEBHOOK (GATEWAY CALLBACK) ------------------

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Signature") String signature
    ) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok("Webhook processed");
    }

    // ------------------ ADMIN ONLY ------------------

    @GetMapping
    public ResponseEntity<List<PaymentEntity>> getAllPayments() {
        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }

    @GetMapping("/invoice/pdf/{paymentId}")
    public ResponseEntity<byte[]> downloadInvoicePdf(
            @PathVariable UUID paymentId
    ) {

        InvoiceEntity invoice = invoiceRepository.findByPaymentId(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Invoice not found"));

        byte[] pdfBytes = InvoicePdfGenerator.generateInvoicePdf(invoice);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice-" + invoice.getInvoiceNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}
