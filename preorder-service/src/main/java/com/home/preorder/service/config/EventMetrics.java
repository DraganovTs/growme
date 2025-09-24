package com.home.preorder.service.config;

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

    // Category Event Metrics
    private final Counter categorySuccessCounter;
    private final Counter categoryFailureCounter;
    private final Counter categoryDuplicateCounter;

    public EventMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // User event metrics
        this.userSuccessCounter = Counter.builder("user.events.processed")
                .tag("result", "success")
                .description("Number of user events processed successfully")
                .register(meterRegistry);
        this.userFailureCounter = Counter.builder("user.events.processed")
                .tag("result", "failure")
                .description("Number of user events that failed processing")
                .register(meterRegistry);
        this.userDuplicateCounter = Counter.builder("user.events.duplicate")
                .description("Number of duplicate user events detected")
                .register(meterRegistry);

        // Category event metrics
        this.categorySuccessCounter = Counter.builder("category.events.processed")
                .tag("result", "success")
                .description("Number of category events processed successfully")
                .register(meterRegistry);
        this.categoryFailureCounter = Counter.builder("category.events.processed")
                .tag("result", "failure")
                .description("Number of category events that failed processing")
                .register(meterRegistry);
        this.categoryDuplicateCounter = Counter.builder("category.events.duplicate")
                .description("Number of duplicate category events detected")
                .register(meterRegistry);
    }

    // User event methods
    public void recordUserSuccess() {
        userSuccessCounter.increment();
    }

    public void recordUserFailure() {
        userFailureCounter.increment();
    }

    public void recordUserDuplicate() {
        userDuplicateCounter.increment();
    }

    // Category event methods
    public void recordCategorySuccess() {
        categorySuccessCounter.increment();
    }

    public void recordCategoryFailure() {
        categoryFailureCounter.increment();
    }

    public void recordCategoryDuplicate() {
        categoryDuplicateCounter.increment();
    }
}