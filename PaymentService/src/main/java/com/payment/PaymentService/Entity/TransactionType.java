package com.payment.PaymentService.Entity;

public enum TransactionType {
    PAYMENT_INITIATED,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    PAYMENT_REFUNDED,
    WEBHOOK_RECEIVED
}
