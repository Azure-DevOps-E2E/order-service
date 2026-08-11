package com.nexuscart.order.domain;

import java.time.Instant;
import java.util.List;

public record Order(
        String id,
        String userId,
        List<OrderItem> items,
        long totalAmount,
        String currency,
        String status,
        Instant createdAt) {

    public Order {
        items = List.copyOf(items);
    }
}
