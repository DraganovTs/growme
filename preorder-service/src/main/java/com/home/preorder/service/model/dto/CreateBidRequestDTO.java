package com.home.preorder.service.model.dto;

import com.home.preorder.service.model.enums.BidStatus;
import com.home.preorder.service.model.enums.DeliveryMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private UUID taskId;  //ok

    @Positive(message = "Bid amount must be greater than zero")
    @DecimalMin(value = "0.01", message = "Minimum bid amount is $0.01")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 digits with 2 decimal places")
    private BigDecimal price; //ok

    @NotBlank(message = "Please describe your offer to the buyer")
    @Size(min = 20, max = 500, message = "Message must be between 20-500 characters")
    private String message;  //ok

    @NotNull(message = "Proposed harvest date is required")
    @Future(message = "Harvest date must be at least tomorrow")
    @FutureOrPresent(message = "Harvest date cannot be in the past")
    private LocalDate proposedHarvestDate; //ok

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please select how you'll deliver the produce")
    private DeliveryMethod deliveryMethod; //ok

    @NotNull(message = "Bid must have user, please log in")
    private UUID userId;  //ok

    private boolean deliveryIncluded; //ok

    @Enumerated
    @NotNull(message = "Bid Status must be fill")
    private BidStatus status;

    @NotNull(message = "user name must not be empty")
    private String userName;
}

