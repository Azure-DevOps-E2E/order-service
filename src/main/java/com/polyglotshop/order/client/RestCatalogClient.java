package com.polyglotshop.order.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.polyglotshop.order.error.ApiException;
import com.polyglotshop.order.request.RequestIdFilter;

@Component
public class RestCatalogClient implements CatalogClient {

    private final RestClient restClient;

    public RestCatalogClient(@Qualifier("catalogRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ProductInfo getProduct(String productId, String requestId) {
        try {
            return restClient.get()
                    .uri("/api/v1/products/{id}", productId)
                    .header(RequestIdFilter.HEADER_NAME, requestId)
                    .retrieve()
                    .body(ProductInfo.class);
        } catch (HttpClientErrorException.NotFound error) {
            throw new ApiException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "PRODUCT_NOT_FOUND",
                    "Product %s does not exist".formatted(productId));
        } catch (RestClientException error) {
            throw new ApiException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "CATALOG_SERVICE_UNAVAILABLE",
                    "Catalog Service is unavailable");
        }
    }
}
