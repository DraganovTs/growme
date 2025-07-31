package com.home.order.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "OrderDTO", description = "Order creation request data")
@Data
@Builder
public class OrderDTO {
    @Schema(description = "Basket ID from which the order is created", example = "basket123")
    @NotBlank(message = "Basket ID cannot be blank")
    private String basketId;
    @Schema(description = "ID of the selected delivery method", example = "1")
    @Min(value = 1, message = "Delivery method ID must be at least 1")
    private int deliveryMethodId;
    @Schema(description = "Shipping address details")
    @NotNull(message = "Shipping address cannot be null")
    @Valid
    private AddressDTO shipToAddress;
    @Schema(description = "Buyer's email address", example = "buyer@example.com")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String userEmail;
}
