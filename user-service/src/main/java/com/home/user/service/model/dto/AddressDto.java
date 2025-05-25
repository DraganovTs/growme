package com.home.user.service.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a physical mailing address
 */
@Data
@Builder
@Schema(
        name = "Address",
        description = "Physical mailing address information"
)
public class AddressDto {

    @Schema(
            description = "Street address including building number",
            example = "123 Main St",
            maxLength = 30,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Street is required")
    @Size(max = 30, message = "Street cannot exceed 30 characters")
    private String street;

    @Schema(
            description = "City or locality name",
            example = "San Francisco",
            maxLength = 30,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "City is required")
    @Size(max = 30, message = "City cannot exceed 30 characters")
    private String city;

    @Schema(
            description = "State or province",
            example = "CA",
            minLength = 2,
            maxLength = 20,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "State is required")
    @Size(min = 2, max = 20, message = "State must be between 2-20 characters")
    private String state;

    @Schema(
            description = "Postal/ZIP code",
            example = "94105",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Zip code is required")
    private String zipCode;
}
