package com.polyglotshop.order.api;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polyglotshop.order.domain.Order;
import com.polyglotshop.order.request.RequestIdFilter;
import com.polyglotshop.order.service.OrderApplicationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderApplicationService service;

    public OrderController(OrderApplicationService service) {
        this.service = service;
    }

    @GetMapping
    OrderListResponse list() {
        return new OrderListResponse(service.findAll());
    }

    @GetMapping("/{id}")
    Order get(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    ResponseEntity<Order> create(
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest) {
        String requestId = httpRequest.getAttribute(RequestIdFilter.ATTRIBUTE_NAME).toString();
        Order created = service.create(request, requestId);
        return ResponseEntity
                .created(URI.create("/api/v1/orders/" + created.id()))
                .body(created);
    }
}
