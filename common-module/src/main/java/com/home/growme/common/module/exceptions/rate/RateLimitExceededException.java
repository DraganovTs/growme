package com.home.growme.common.module.exceptions.rate;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends BaseException {
    public RateLimitExceededException() {
        super("API rate limit exceeded", "RATE_LIMIT_EXCEEDED", HttpStatus.TOO_MANY_REQUESTS);
    }
}