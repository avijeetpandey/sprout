package com.avijeet.sprout.kafka;

import com.avijeet.sprout.entities.Order;
import com.avijeet.sprout.entities.Payment;
import com.avijeet.sprout.enums.OrderStatus;
import com.avijeet.sprout.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-success-topic", groupId = "order-group")
    public void handlePaymentSuccess(Payment payment) {
        Order order = orderRepository.findById(payment.getOrderId()).orElse(null);
        if (order != null) {
            order.setStatus(OrderStatus.PLACED);
            orderRepository.save(order);
        }
    }
}