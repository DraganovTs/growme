package com.home.growme.common.module.events;

import java.time.Instant;
import java.util.UUID;

public abstract class Event {
    private final String eventId = UUID.randomUUID().toString();
    private final Instant timestamp = Instant.now();
    //TODO add tracing fields
}
