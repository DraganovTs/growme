package com.home.user.service.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class EventMetrics {
    private final MeterRegistry meterRegistry;

    // Specific Event Type Metrics
    private final Counter roleAssignmentSuccessCounter;
    private final Counter roleAssignmentFailureCounter;
    private final Counter productAssignmentSuccessCounter;
    private final Counter productAssignmentFailureCounter;
    private final Counter productDeletionSuccessCounter;
    private final Counter productDeletionFailureCounter;
    private final Counter orderCompletedSuccessCounter;
    private final Counter orderCompletedFailureCounter;

    public EventMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;


        // Role assignment events
        this.roleAssignmentSuccessCounter = Counter.builder("user.service.events.role.assignment")
                .tag("result", "success")
                .description("Successful role assignment events")
                .register(meterRegistry);

        this.roleAssignmentFailureCounter = Counter.builder("user.service.events.role.assignment")
                .tag("result", "failure")
                .description("Failed role assignment events")
                .register(meterRegistry);

        // Product assignment events
        this.productAssignmentSuccessCounter = Counter.builder("user.service.events.product.assignment")
                .tag("result", "success")
                .description("Successful product assignment events")
                .register(meterRegistry);

        this.productAssignmentFailureCounter = Counter.builder("user.service.events.product.assignment")
                .tag("result", "failure")
                .description("Failed product assignment events")
                .register(meterRegistry);

        // Product deletion events
        this.productDeletionSuccessCounter = Counter.builder("user.service.events.product.deletion")
                .tag("result", "success")
                .description("Successful product deletion events")
                .register(meterRegistry);

        this.productDeletionFailureCounter = Counter.builder("user.service.events.product.deletion")
                .tag("result", "failure")
                .description("Failed product deletion events")
                .register(meterRegistry);

        // Order completed events
        this.orderCompletedSuccessCounter = Counter.builder("user.service.events.order.completed")
                .tag("result", "success")
                .description("Successful order completed events")
                .register(meterRegistry);

        this.orderCompletedFailureCounter = Counter.builder("user.service.events.order.completed")
                .tag("result", "failure")
                .description("Failed order completed events")
                .register(meterRegistry);
    }



    // Specific event type methods
    public void recordRoleAssignmentSuccess() {
        roleAssignmentSuccessCounter.increment();

    }

    public void recordRoleAssignmentFailure() {
        roleAssignmentFailureCounter.increment();

    }

    public void recordProductAssignmentSuccess() {
        productAssignmentSuccessCounter.increment();

    }

    public void recordProductAssignmentFailure() {
        productAssignmentFailureCounter.increment();

    }

    public void recordProductDeletionSuccess() {
        productDeletionSuccessCounter.increment();

    }

    public void recordProductDeletionFailure() {
        productDeletionFailureCounter.increment();

    }

    public void recordOrderCompletedSuccess() {
        orderCompletedSuccessCounter.increment();

    }

    public void recordOrderCompletedFailure() {
        orderCompletedFailureCounter.increment();

    }
}