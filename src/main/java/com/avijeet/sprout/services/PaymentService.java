package com.avijeet.sprout.services;

import com.avijeet.sprout.dto.OrderNotificationEvent;
import com.avijeet.sprout.dto.PaymentWebhookRequest;
import com.avijeet.sprout.entities.Order;
import com.avijeet.sprout.entities.Payment;
import com.avijeet.sprout.entities.User;
import com.avijeet.sprout.enums.PaymentStatus;
import com.avijeet.sprout.exceptions.PaymentException;
import com.avijeet.sprout.repository.OrderRepository;
import com.avijeet.sprout.repository.PaymentRepository;
import com.avijeet.sprout.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public String initiatePayment(Long orderId, Double amount) {
        log.info("Initiating payment for order id: {}", orderId);
        String txId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .orderId(orderId)
                .transactionId(txId)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        return txId;
    }

    @Transactional
    public void processWebhook(PaymentWebhookRequest request) {
        Payment payment = paymentRepository.findByTransactionId(request.transactionId())
                .orElseThrow(() -> new PaymentException("Transaction not found"));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            log.warn("Payment {} already processed.", request.transactionId());
            return;
        }

        if ("SUCCESS".equalsIgnoreCase(request.status())) {
            payment.setStatus(PaymentStatus.COMPLETED);
            kafkaTemplate.send("payment-success-topic", payment.getOrderId().toString(), payment);
            publishOrderNotification(payment.getOrderId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            kafkaTemplate.send("payment-failed-topic", payment.getOrderId().toString(), payment);
        }

        payment.setProviderResponse(request.providerMessage());
        paymentRepository.save(payment);
        log.info("Payment status updated to {} for tx: {}", payment.getStatus(), payment.getTransactionId());
    }

    private void publishOrderNotification(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new PaymentException("Order not found for payment notification"));

        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new PaymentException("User not found for payment notification"));

        kafkaTemplate.send(
                "order-notification",
                order.getOrderNumber(),
                new OrderNotificationEvent(user.getEmail(), order.getOrderNumber())
        );
    }
}
