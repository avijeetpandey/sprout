package com.avijeet.sprout.kafka;

import com.avijeet.sprout.dto.OrderNotificationEvent;
import com.avijeet.sprout.services.NotificationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
public class OrderNotificationConsumer {
    private final NotificationService notificationService;

    @PostConstruct
    public void init() {
        log.info("🚀 OrderNotificationConsumer: BEAN ALIVE");
    }

    @KafkaListener(
            topics = "order-notification",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumerOrderNotification(OrderNotificationEvent event) {
        log.info("📥 KAFKA RECEIVED: Order #{}", event.orderNumber());
        try {
            notificationService.sendOrderConfirmation(event);
            log.info("✅ CONSUMED: Order #{}", event.orderNumber());
        } catch (Exception e) {
            log.error("❌ FAILED: Processing order #{}. Error: {}", event.orderNumber(), e.getMessage());
        }
    }
}