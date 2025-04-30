package com.home.growme.common.module.exceptions.system;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class CacheOperationException extends BaseException {
    public CacheOperationException(String operation) {
        super("Cache operation failed: " + operation,
                "CACHE_OPERATION_FAILED",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}