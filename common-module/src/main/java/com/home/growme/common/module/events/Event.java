package com.home.growme.common.module.events;

import java.time.Instant;
import java.util.UUID;

public abstract class Event {

    private final String eventId = UUID.randomUUID().toString();
    private final Instant timestamp = Instant.now();

    // TODO: Add distributed tracing fields (e.g., traceId, spanId)

    public String getEventId() {
        return eventId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}