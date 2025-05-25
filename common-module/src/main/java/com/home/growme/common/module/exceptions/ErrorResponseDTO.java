package com.home.growme.common.module.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Standard error response format for API exceptions.
 */
@Data
@Builder
@Schema(
        name = "ErrorResponse",
        description = """
        Standardized error response format returned by the API
        when exceptions or validation errors occur.
        """
)
public class ErrorResponseDTO {

    @Schema(
            description = "API path where the error occurred",
            example = "/api/users/sync",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String apiPath;

    @Schema(
            description = "HTTP status code",
            example = "400",
            implementation = HttpStatus.class,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private HttpStatus status;

    @Schema(
            description = "Application-specific error code",
            example = "USER-0001",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String errorCode;

    @Schema(
            description = "Human-readable error message",
            example = "Validation failed: Email must be valid",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String message;

    @Schema(
            description = "Timestamp when the error occurred",
            example = "2023-07-15T12:34:56.789Z",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime timestamp;
}
