package com.polyglotshop.order.api;

import java.util.List;

import com.polyglotshop.order.domain.Order;

public record OrderListResponse(List<Order> items) {

    public OrderListResponse {
        items = List.copyOf(items);
    }
}
