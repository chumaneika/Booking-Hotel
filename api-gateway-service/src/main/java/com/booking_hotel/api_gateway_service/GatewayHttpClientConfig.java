package com.booking_hotel.api_gateway_service;

import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class GatewayHttpClientConfig {
    @Bean
    HttpClientCustomizer containerDnsResolver() {
        // Docker can assign a different IP even when a container is restarted.
        return client -> client.resolver(spec -> spec
                .cacheMinTimeToLive(Duration.ZERO)
                .cacheMaxTimeToLive(Duration.ofSeconds(5))
                .cacheNegativeTimeToLive(Duration.ofSeconds(1)));
    }
}
