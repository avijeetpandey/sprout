package com.avijeet.sprout.services;

import com.avijeet.sprout.dto.CartDto;
import com.avijeet.sprout.dto.OrderNotificationEvent;
import com.avijeet.sprout.dto.OrderRequestDto;
import com.avijeet.sprout.dto.OrderResponseDto;
import com.avijeet.sprout.entities.Order;
import com.avijeet.sprout.entities.OrderItem;
import com.avijeet.sprout.entities.Product;
import com.avijeet.sprout.enums.OrderStatus;
import com.avijeet.sprout.repository.OrderRepository;
import com.avijeet.sprout.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectProvider<KafkaTemplate<String, Object>> kafkaTemplateProvider;

    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto requestDto) {
        String cartKey = "cart:" + requestDto.userId();
        Object rawCart = redisTemplate.opsForValue().get(cartKey);

        if(rawCart == null) {
            throw new IllegalStateException("Cart is empty or does not exist");
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        CartDto cart = mapper.convertValue(rawCart, CartDto.class);

        log.info("Processing payment for user {}", requestDto.userId());
        boolean paymentSuccess = true;

        if(!paymentSuccess) {
            throw new RuntimeException("Payment failed");
        }

        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .status(OrderStatus.PLACED)
                .userId(requestDto.userId())
                .createdAt(LocalDateTime.now())
                .totalAmount(cart.totalAmount())
                .build();

        List<OrderItem> orderItems = cart.items().stream().map(item -> {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if(product.getStockQuantity() < item.quantity()) {
                throw new RuntimeException("Insufficient stocks for " + product.getId());
            }

            product.setStockQuantity(product.getStockQuantity() - item.quantity());
            productRepository.save(product);

            return OrderItem.builder()
                    .productId(item.productId())
                    .quantity(item.quantity())
                    .price(item.price())
                    .order(order)
                    .build();
        }).toList();

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        redisTemplate.delete(cartKey);

        KafkaTemplate<String, Object> kafkaTemplate = kafkaTemplateProvider.getIfAvailable();
        if (kafkaTemplate != null) {
            kafkaTemplate.send("order-notification", new OrderNotificationEvent(requestDto.email(), savedOrder.getOrderNumber()));
        } else {
            log.info("Kafka is disabled; skipping order notification publish for order {}", savedOrder.getOrderNumber());
        }

        return new OrderResponseDto(savedOrder.getOrderNumber(), savedOrder.getTotalAmount(), savedOrder.getStatus(), savedOrder.getCreatedAt());
    }

    public OrderResponseDto trackOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderResponseDto(order.getOrderNumber(), order.getTotalAmount(), order.getStatus(), order.getCreatedAt());
    }
}
