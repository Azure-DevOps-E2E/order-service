package com.polyglotshop.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.polyglotshop.order.api.CreateOrderItemRequest;
import com.polyglotshop.order.api.CreateOrderRequest;
import com.polyglotshop.order.client.CatalogClient;
import com.polyglotshop.order.client.UserClient;
import com.polyglotshop.order.domain.Order;
import com.polyglotshop.order.domain.OrderItem;
import com.polyglotshop.order.error.ApiException;
import com.polyglotshop.order.repository.OrderRepository;

class OrderApplicationServiceTest {

    private final UserClient userClient = (id, requestId) ->
            new UserClient.UserInfo(id, "Test User", "test@example.com");

    private final CatalogClient catalogClient = (id, requestId) ->
            new CatalogClient.ProductInfo(
                    id,
                    "Keyboard",
                    "Test product",
                    1_290_000,
                    "VND",
                    10,
                    "amber");

    private final StubOrderRepository repository = new StubOrderRepository();
    private final OrderApplicationService service =
            new OrderApplicationService(userClient, catalogClient, repository);

    @Test
    void createsOrderWithServerCalculatedTotalsAndSnapshot() {
        Order result = service.create(
                new CreateOrderRequest(
                        "usr-001",
                        List.of(new CreateOrderItemRequest("prd-001", 2))),
                "request-1");

        assertThat(result.totalAmount()).isEqualTo(2_580_000);
        assertThat(result.currency()).isEqualTo("VND");
        assertThat(result.items()).singleElement().satisfies(item -> {
            assertThat(item.productName()).isEqualTo("Keyboard");
            assertThat(item.unitPrice()).isEqualTo(1_290_000);
            assertThat(item.lineTotal()).isEqualTo(2_580_000);
        });
    }

    @Test
    void rejectsDuplicateProductsBeforeCallingCatalog() {
        var request = new CreateOrderRequest(
                "usr-001",
                List.of(
                        new CreateOrderItemRequest("prd-001", 1),
                        new CreateOrderItemRequest("prd-001", 2)));

        assertThatThrownBy(() -> service.create(request, "request-2"))
                .isInstanceOfSatisfying(ApiException.class, error -> {
                    assertThat(error.status()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(error.code()).isEqualTo("DUPLICATE_PRODUCT");
                });
    }

    @Test
    void rejectsQuantityAboveStock() {
        var request = new CreateOrderRequest(
                "usr-001",
                List.of(new CreateOrderItemRequest("prd-001", 11)));

        assertThatThrownBy(() -> service.create(request, "request-3"))
                .isInstanceOfSatisfying(ApiException.class, error -> {
                    assertThat(error.status()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(error.code()).isEqualTo("INSUFFICIENT_STOCK");
                });
    }

    private static class StubOrderRepository implements OrderRepository {

        private final List<Order> orders = new ArrayList<>();

        @Override
        public Order create(
                String userId,
                List<OrderItem> items,
                long totalAmount,
                String currency) {
            Order order = new Order(
                    "ord-001",
                    userId,
                    items,
                    totalAmount,
                    currency,
                    "CREATED",
                    java.time.Instant.parse("2026-08-11T08:30:00Z"));
            orders.add(order);
            return order;
        }

        @Override
        public List<Order> findAll() {
            return List.copyOf(orders);
        }

        @Override
        public Optional<Order> findById(String id) {
            return orders.stream().filter(order -> order.id().equals(id)).findFirst();
        }
    }
}
