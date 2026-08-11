package com.nexuscart.order.api;

import java.util.List;

import com.nexuscart.order.domain.Order;

public record OrderListResponse(List<Order> items) {

    public OrderListResponse {
        items = List.copyOf(items);
    }
}
