package com.nexuscart.order.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final String version;
    private final String imageTag;

    public HealthController(
            @Value("${app.version}") String version,
            @Value("${app.image-tag:${app.version}}") String imageTag) {
        this.version = version;
        this.imageTag = imageTag;
    }

    @GetMapping("/health")
    Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "order-service",
                "version", version,
                "imageTag", imageTag);
    }

    @GetMapping("/liveness")
    Map<String, String> liveness() {
        return Map.of(
                "status", "UP",
                "service", "order-service");
    }
}
