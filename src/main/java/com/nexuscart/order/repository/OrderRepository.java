package com.nexuscart.order.repository;

import java.util.List;
import java.util.Optional;

import com.nexuscart.order.domain.Order;
import com.nexuscart.order.domain.OrderItem;

public interface OrderRepository {

    Order create(String userId, List<OrderItem> items, long totalAmount, String currency);

    List<Order> findAll();

    Optional<Order> findById(String id);
}
