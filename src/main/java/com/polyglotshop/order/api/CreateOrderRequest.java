package com.polyglotshop.order.api;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CreateOrderRequest(
        @NotBlank String userId,
        @NotEmpty List<@Valid CreateOrderItemRequest> items) {
}
