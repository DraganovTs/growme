package com.home.order.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "payment")
@Data
public class PaymentProperties {
    private Duration responseTimeout = Duration.ofSeconds(15);
    private Duration initialRetryDelay = Duration.ofMillis(500);
    private int maxRetryAttempts = 3;
}
