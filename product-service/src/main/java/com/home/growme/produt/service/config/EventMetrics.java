package com.home.growme.produt.service.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class EventMetrics {
    private final MeterRegistry meterRegistry;
    // Consumer Metrics
    // User Event Metrics
    private final Counter userSuccessCounter;
    private final Counter userFailureCounter;
    private final Counter userDuplicateCounter;
    // Order Event Metrics
    private final Counter orderSuccessCounter;
    private final Counter orderFailureCounter;
    // Publisher Metrics
    //Product Metrics
    private final Counter productAssignmentPublishSuccess;
    private final Counter productAssignmentPublishFailure;
    private final Counter productDeletionPublishSuccess;
    private final Counter productDeletionPublishFailure;
    //Category Metrics
    private final Counter categoryCreationPublishSuccess;
    private final Counter categoryCreationPublishFailure;

    // Timer metrics for publishing latency
    private final Timer productAssignmentPublishTimer;
    private final Timer productDeletionPublishTimer;
    private final Timer categoryCreationPublishTimer;

    public EventMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        // Consumer metrics
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

        // Publisher metrics
        this.productAssignmentPublishSuccess = Counter.builder("product.assignment.published")
                .tag("result", "success")
                .register(meterRegistry);
        this.productAssignmentPublishFailure = Counter.builder("product.assignment.published")
                .tag("result", "failure")
                .register(meterRegistry);

        this.productDeletionPublishSuccess = Counter.builder("product.deletion.published")
                .tag("result", "success")
                .register(meterRegistry);
        this.productDeletionPublishFailure = Counter.builder("product.deletion.published")
                .tag("result", "failure")
                .register(meterRegistry);

        this.categoryCreationPublishSuccess = Counter.builder("category.creation.published")
                .tag("result", "success")
                .register(meterRegistry);
        this.categoryCreationPublishFailure = Counter.builder("category.creation.published")
                .tag("result", "failure")
                .register(meterRegistry);

        // Timer metrics
        this.productAssignmentPublishTimer = Timer.builder("product.assignment.publish.duration")
                .register(meterRegistry);
        this.productDeletionPublishTimer = Timer.builder("product.deletion.publish.duration")
                .register(meterRegistry);
        this.categoryCreationPublishTimer = Timer.builder("category.creation.publish.duration")
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

    // Order event methods
    public void recordOrderSuccess() {
        orderSuccessCounter.increment();
    }

    public void recordOrderFailure() {
        orderFailureCounter.increment();
    }

    // Publisher event methods (new)
    public void recordProductAssignmentPublishSuccess() {
        productAssignmentPublishSuccess.increment();
    }

    public void recordProductAssignmentPublishFailure() {
        productAssignmentPublishFailure.increment();
    }

    public void recordProductDeletionPublishSuccess() {
        productDeletionPublishSuccess.increment();
    }

    public void recordProductDeletionPublishFailure() {
        productDeletionPublishFailure.increment();
    }

    public void recordCategoryCreationPublishSuccess() {
        categoryCreationPublishSuccess.increment();
    }

    public void recordCategoryCreationPublishFailure() {
        categoryCreationPublishFailure.increment();
    }

    // Timer methods
    public Timer.Sample startProductAssignmentPublishTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopProductAssignmentPublishTimer(Timer.Sample sample) {
        sample.stop(productAssignmentPublishTimer);
    }

    public Timer.Sample startProductDeletionPublishTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopProductDeletionPublishTimer(Timer.Sample sample) {
        sample.stop(productDeletionPublishTimer);
    }

    public Timer.Sample startCategoryCreationPublishTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopCategoryCreationPublishTimer(Timer.Sample sample) {
        sample.stop(categoryCreationPublishTimer);
    }
}
