package com.home.growme.produt.service.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class EventMetrics {
    private final MeterRegistry meterRegistry;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Counter duplicateCounter;

    public EventMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.successCounter = Counter.builder("user.events.processed")
                .tag("result", "success")
                .register(meterRegistry);
        this.failureCounter = Counter.builder("user.events.processed")
                .tag("result", "failure")
                .register(meterRegistry);
        this.duplicateCounter = Counter.builder("user.events.duplicate")
                .register(meterRegistry);
    }

    public void recordSuccess() { successCounter.increment(); }
    public void recordFailure() { failureCounter.increment(); }
    public void recordDuplicate() { duplicateCounter.increment(); }
}
