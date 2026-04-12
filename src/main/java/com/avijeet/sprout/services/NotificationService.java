package com.avijeet.sprout.services;

import com.avijeet.sprout.dto.OrderNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {
    public void sendOrderConfirmation(OrderNotificationEvent event) {
        log.info("📧 Sending email to {}: Your order {} has been placed successfully!",
                event.email(), event.orderNumber());
    }
}
