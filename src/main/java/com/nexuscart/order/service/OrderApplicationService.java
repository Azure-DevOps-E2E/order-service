package com.nexuscart.order.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.nexuscart.order.api.CreateOrderItemRequest;
import com.nexuscart.order.api.CreateOrderRequest;
import com.nexuscart.order.client.CatalogClient;
import com.nexuscart.order.client.UserClient;
import com.nexuscart.order.domain.Order;
import com.nexuscart.order.domain.OrderItem;
import com.nexuscart.order.error.ApiException;
import com.nexuscart.order.repository.OrderRepository;

@Service
public class OrderApplicationService {

    private final UserClient userClient;
    private final CatalogClient catalogClient;
    private final OrderRepository repository;

    public OrderApplicationService(
            UserClient userClient,
            CatalogClient catalogClient,
            OrderRepository repository) {
        this.userClient = userClient;
        this.catalogClient = catalogClient;
        this.repository = repository;
    }

    public Order create(CreateOrderRequest request, String requestId) {
        rejectDuplicateProducts(request.items());
        userClient.getUser(request.userId(), requestId);

        List<OrderItem> items = request.items().stream()
                .map(item -> toOrderItem(item, requestId))
                .toList();

        long totalAmount;
        try {
            totalAmount = items.stream()
                    .mapToLong(OrderItem::lineTotal)
                    .reduce(0L, Math::addExact);
        } catch (ArithmeticException error) {
            throw new ApiException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "ORDER_TOTAL_OVERFLOW",
                    "Order total is too large");
        }

        return repository.create(request.userId(), items, totalAmount, "VND");
    }

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Order findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "ORDER_NOT_FOUND",
                        "Order %s does not exist".formatted(id)));
    }

    private OrderItem toOrderItem(CreateOrderItemRequest item, String requestId) {
        var product = catalogClient.getProduct(item.productId(), requestId);
        if (item.quantity() > product.stock()) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "INSUFFICIENT_STOCK",
                    "Product %s only has %d item(s) in stock"
                            .formatted(product.id(), product.stock()));
        }
        if (!"VND".equals(product.currency())) {
            throw new ApiException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "UNSUPPORTED_CURRENCY",
                    "Product %s does not use VND".formatted(product.id()));
        }

        long lineTotal;
        try {
            lineTotal = Math.multiplyExact(product.price(), item.quantity());
        } catch (ArithmeticException error) {
            throw new ApiException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "ORDER_TOTAL_OVERFLOW",
                    "Order total is too large");
        }

        return new OrderItem(
                product.id(),
                product.name(),
                product.price(),
                item.quantity(),
                lineTotal);
    }

    private void rejectDuplicateProducts(List<CreateOrderItemRequest> items) {
        Set<String> productIds = new HashSet<>();
        for (CreateOrderItemRequest item : items) {
            if (!productIds.add(item.productId())) {
                throw new ApiException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "DUPLICATE_PRODUCT",
                        "Product %s appears more than once".formatted(item.productId()));
            }
        }
    }
}
