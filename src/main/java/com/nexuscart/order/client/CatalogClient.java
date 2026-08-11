package com.nexuscart.order.client;

public interface CatalogClient {

    ProductInfo getProduct(String productId, String requestId);

    record ProductInfo(
            String id,
            String name,
            String description,
            long price,
            String currency,
            int stock,
            String accent) {
    }
}
