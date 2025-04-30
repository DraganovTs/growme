package com.home.growme.common.module.exceptions.system;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ExternalServiceIntegrationException extends BaseException {
    public ExternalServiceIntegrationException(String serviceName) {
        super("Failed to communicate with " + serviceName,
                "EXTERNAL_SERVICE_FAILURE",
                HttpStatus.BAD_GATEWAY);
    }
}