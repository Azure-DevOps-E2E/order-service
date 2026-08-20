package com.nexuscart.order.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

class HealthControllerTest {

    @Test
    void healthIncludesServiceVersion() {
        HealthController controller = new HealthController("1.2.3-test", "order-test-tag");

        assertThat(controller.health()).isEqualTo(Map.of(
                "status", "UP",
                "service", "order-service",
                "version", "1.2.3-test",
                "imageTag", "order-test-tag"));
    }
}
