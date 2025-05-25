package com.home.growme.produt.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
/**
 * Represents a product owner in the system.
 * Owners are users who list products in the marketplace.
 */
@Schema(name = "Owner", description = "Represents the product owner or seller")
@Data
@Builder
public class OwnerDTO {
    @Schema(
            description = "Unique identifier of the owner",
            example = "d290f1ee-6c54-4b01-90e6-d701748f0851",
            format = "uuid"
    )
    private UUID ownerId;
    @Schema(
            description = "Name of the product owner",
            example = "John Doe",
            maxLength = 100
    )
    @NotBlank(message = "Owner name must not be blank")
    @Size(max = 30, message = "Owner name must be at most 30 characters")
    private String ownerName;
}
