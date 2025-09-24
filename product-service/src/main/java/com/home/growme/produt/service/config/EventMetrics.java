package com.home.growme.produt.service.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class EventMetrics {
    private final MeterRegistry meterRegistry;
    // User Event Metrics
    private final Counter userSuccessCounter;
    private final Counter userFailureCounter;
    private final Counter userDuplicateCounter;
    // Order Event Metrics
    private final Counter orderSuccessCounter;
    private final Counter orderFailureCounter;

    public EventMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.userSuccessCounter = Counter.builder("user.events.processed")
                .tag("result", "success")
                .register(meterRegistry);
        this.userFailureCounter = Counter.builder("user.events.processed")
                .tag("result", "failure")
                .register(meterRegistry);
        this.userDuplicateCounter = Counter.builder("user.events.duplicate")
                .register(meterRegistry);

        this.orderSuccessCounter = Counter.builder("order.events.processed")
                .tag("result", "success")
                .register(meterRegistry);
        this.orderFailureCounter = Counter.builder("order.events.processed")
                .tag("result", "failure")
                .register(meterRegistry);
    }

    // User event methods
    public void recordUserSuccess() { userSuccessCounter.increment(); }
    public void recordUserFailure() { userFailureCounter.increment(); }
    public void recordUserDuplicate() { userDuplicateCounter.increment(); }

    // Order event methods
    public void recordOrderSuccess() {
        orderSuccessCounter.increment();
    }

    public void recordOrderFailure() {
        orderFailureCounter.increment();
    }
}
