package com.home.order.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Schema(name = "AddressDTO", description = "Shipping address with recipient name")
@Data
@Builder
public class AddressDTO {
    @Schema(description = "Recipient's first name", example = "John", maxLength = 30)
    @NotBlank(message = "First name cannot be blank")
    @Size(max = 30, message = "First name cannot exceed 30 characters")
    private String firstName;

    @Schema(description = "Recipient's last name", example = "Doe", maxLength = 30)
    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 30, message = "Last name cannot exceed 30 characters")
    private String lastName;

    @Schema(description = "Street address including building number", example = "123 Main St", maxLength = 30)
    @NotBlank(message = "Street address cannot be blank")
    @Size(max = 30, message = "Street address cannot exceed 30 characters")
    private String street;

    @Schema(description = "City or locality", example = "San Francisco", maxLength = 30)
    @NotBlank(message = "City cannot be blank")
    @Size(max = 30, message = "City cannot exceed 30 characters")
    private String city;

    @Schema(description = "State or province", example = "CA", minLength = 2, maxLength = 20)
    @NotBlank(message = "State cannot be blank")
    @Size(min = 2, max = 20, message = "State must be 20 characters")
    private String state;

    @Schema(description = "Postal or ZIP code", example = "94105")
    @NotBlank(message = "Zip code cannot be blank")
    private String zipCode;
}
