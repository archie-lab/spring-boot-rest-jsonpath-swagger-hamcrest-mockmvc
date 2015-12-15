package example.service;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * This is an optional class used to inject application specific health check
 * into the Spring Boot health management endpoint.
 */
@Component
public class ProductServiceHealth implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up().withDetail("details", "{Some Details}").status("OK status").build();
    }
}
