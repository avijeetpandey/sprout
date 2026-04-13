package com.avijeet.sprout.controllers;

import com.avijeet.sprout.config.api.ApiResponse;
import com.avijeet.sprout.config.controller.BaseController;
import com.avijeet.sprout.dto.PaymentWebhookRequest;
import com.avijeet.sprout.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentWebhookController extends BaseController {
    private final PaymentService paymentService;

    @PostMapping("/webhook/simulate")
    public ResponseEntity<ApiResponse<String>> simulateWebhook(@RequestBody PaymentWebhookRequest request) {
        paymentService.processWebhook(request);
        return ok("Webhook Processed Successfully", "Transaction " + request.transactionId() + " updated.");
    }
}