package com.home.growme.common.module.exceptions.eventPublishing;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class EventPublishingException extends BaseException {

    public EventPublishingException(String message) {
        super(message, "EVENT_PUBLISHING_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public EventPublishingException(String message, Throwable cause) {
        super(message, "EVENT_PUBLISHING_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
