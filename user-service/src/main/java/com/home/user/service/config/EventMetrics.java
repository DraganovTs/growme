package com.home.user.service.config;

import com.home.user.service.model.enums.EventType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class EventMetrics {
    private final Map<EventType, Counter> successCounters = new EnumMap<>(EventType.class);
    private final Map<EventType, Counter> failureCounters = new EnumMap<>(EventType.class);


    public EventMetrics(MeterRegistry registry) {
        for (EventType type : EventType.values()) {
            successCounters.put(type, Counter.builder(type.metricName())
                    .tag("result", "success")
                    .description("Successful " + type.description() + " events")
                    .register(registry));

            failureCounters.put(type, Counter.builder(type.metricName())
                    .tag("result", "failure")
                    .description("Failed " + type.description() + " events")
                    .register(registry));
        }
    }

    public void recordSuccess(EventType type) {
        successCounters.get(type).increment();
    }

    public void recordFailure(EventType type) {
        failureCounters.get(type).increment();
    }

}