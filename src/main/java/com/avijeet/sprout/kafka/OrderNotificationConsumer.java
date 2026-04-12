package com.avijeet.sprout.kafka;

import com.avijeet.sprout.dto.OrderNotificationEvent;
import com.avijeet.sprout.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class OrderNotificationConsumer {
    private final NotificationService notificationService;

    @KafkaListener(
            topics = "order-notification",
            groupId = "sprout-notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumerOrderNotification(OrderNotificationEvent event) {
        log.info("Recieved Kafka message{}", event.orderNumber());
        try {
            notificationService.sendOrderConfirmation(event);
        } catch (Exception e) {
            log.error("Failed to process notification{}", event.orderNumber());
        }
    }
}
