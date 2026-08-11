package com.nexuscart.order.repository;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.nexuscart.order.domain.Order;
import com.nexuscart.order.domain.OrderItem;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final ConcurrentMap<String, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private final Clock clock;

    public InMemoryOrderRepository() {
        this(Clock.systemUTC());
    }

    InMemoryOrderRepository(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Order create(
            String userId,
            List<OrderItem> items,
            long totalAmount,
            String currency) {
        String id = "ord-%03d".formatted(sequence.incrementAndGet());
        Order order = new Order(
                id,
                userId,
                items,
                totalAmount,
                currency,
                "CREATED",
                Instant.now(clock));
        orders.put(id, order);
        return order;
    }

    @Override
    public List<Order> findAll() {
        return orders.values().stream()
                .sorted(Comparator.comparing(Order::createdAt).reversed())
                .toList();
    }

    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }
}
