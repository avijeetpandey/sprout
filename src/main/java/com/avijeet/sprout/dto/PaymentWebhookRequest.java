package com.avijeet.sprout.dto;

public record PaymentWebhookRequest(
        String transactionId,
        Long orderId,
        String status,
        String providerMessage
) { }
