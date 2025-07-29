package com.home.order.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(name = "DeliveryMethodDTO", description = "Details about a delivery method")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryMethodDTO {
    @Schema(description = "Unique delivery method ID", example = "1")
    @NotNull(message = "Delivery method ID cannot be null")
    private Integer deliveryMethodId;
    @Schema(description = "Short name of delivery method", example = "Express")
    @NotBlank(message = "Short name cannot be blank")
    @Size(max = 30, message = "Short name must be less than or equal to 30 characters")
    private String shortName;
    @NotBlank(message = "Delivery time cannot be blank")
    @Size(max = 50, message = "Delivery time must be less than or equal to 50 characters")
    @Schema(description = "Estimated delivery time", example = "1-2 business days")
    private String deliveryTime;
    @Schema(description = "Description of delivery method", example = "Fast delivery with tracking")
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 100, message = "Description must be less than or equal to 100 characters")
    private String description;
    @Schema(description = "Price of the delivery method", example = "9.99")
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
}
