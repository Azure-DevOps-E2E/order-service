package com.nexuscart.order.domain;

public record OrderItem(
        String productId,
        String productName,
        long unitPrice,
        int quantity,
        long lineTotal) {
}
