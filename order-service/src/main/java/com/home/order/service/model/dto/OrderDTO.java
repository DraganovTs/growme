package com.home.order.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDTO {
    @JsonProperty("basketId")
    @NotBlank(message = "Basket ID cannot be blank")
    private String basketId;
    @JsonProperty("deliveryMethodId")
    @Min(value = 1, message = "Delivery method ID must be at least 1")
    private int deliveryMethodId;
    @JsonProperty("shipToAddress")
    @NotNull(message = "Shipping address cannot be null")
    @Valid
    private AddressDTO shipToAddress;
    @JsonProperty("userEmail")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String userEmail;
}
