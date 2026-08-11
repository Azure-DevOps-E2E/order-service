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
public class RestUserClient implements UserClient {

    private final RestClient restClient;

    public RestUserClient(@Qualifier("userRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public UserInfo getUser(String userId, String requestId) {
        try {
            return restClient.get()
                    .uri("/api/v1/users/{id}", userId)
                    .header(RequestIdFilter.HEADER_NAME, requestId)
                    .retrieve()
                    .body(UserInfo.class);
        } catch (HttpClientErrorException.NotFound error) {
            throw new ApiException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "USER_NOT_FOUND",
                    "User %s does not exist".formatted(userId));
        } catch (RestClientException error) {
            throw new ApiException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "USER_SERVICE_UNAVAILABLE",
                    "User Service is unavailable");
        }
    }
}
