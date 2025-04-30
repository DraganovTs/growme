package com.home.growme.common.module.exceptions;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponseDTO {

    private String apiPath;
    private HttpStatus status;
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;


}
