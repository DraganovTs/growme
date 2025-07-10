package com.home.preorder.service.model.dto;

import com.home.preorder.service.model.enums.DeliveryMethod;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class CreateBidRequestDTO {
    @NotNull(message = "Task reference is required")
    UUID taskId;

    @Positive(message = "Bid amount must be greater than zero")
    @DecimalMin(value = "0.01", message = "Minimum bid amount is $0.01")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 digits with 2 decimal places")
    BigDecimal price;

    @NotBlank(message = "Please describe your offer to the buyer")
    @Size(min = 20, max = 500, message = "Message must be between 20-500 characters")
    String message;

    @NotNull(message = "Proposed harvest date is required")
    @Future(message = "Harvest date must be at least tomorrow")
    @FutureOrPresent(message = "Harvest date cannot be in the past")
    LocalDate proposedHarvestDate;

    @NotNull(message = "Please select how you'll deliver the produce")
    DeliveryMethod deliveryMethod;

    boolean deliveryIncluded;
}
