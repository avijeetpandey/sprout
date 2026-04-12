package com.avijeet.sprout.dto;

public record OrderNotificationEvent(
        String email,
        String orderNumber
) { }
