package com.nexuscart.order.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @Qualifier("userRestClient")
    RestClient userRestClient(
            RestClient.Builder builder,
            @Value("${services.user.base-url}") String baseUrl) {
        return client(builder, baseUrl);
    }

    @Bean
    @Qualifier("catalogRestClient")
    RestClient catalogRestClient(
            RestClient.Builder builder,
            @Value("${services.catalog.base-url}") String baseUrl) {
        return client(builder, baseUrl);
    }

    private RestClient client(RestClient.Builder builder, String baseUrl) {
        var requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        return builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
