package com.home.preorder.service.model.dto;

import com.home.preorder.service.model.enums.TaskStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TaskDTO {
    private UUID taskId;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank @Size(max = 1000)
    private String description;

    @NotNull
    private String categoryName;

    private TaskStatus status;

    @NotNull
    private UUID userId;

    @DecimalMin("0.0")
    private BigDecimal budget;

    @Future
    private LocalDateTime deadline;

    @NotNull(message = "Quantity is required")
    @Min(1) @Max(10000)
    private Integer quantity;

    @NotBlank(message = "Unit is required")
    @Size(max = 20)
    private String unit;

    @NotBlank(message = "Quality standard is required")
    @Size(max = 50)
    private String quality;

    @NotNull(message = "Harvest date is required")
    @Future
    private LocalDate harvestDate;

    @NotNull(message = "Delivery date is required")
    private LocalDate deliveryDate;

    private boolean flexibleDates;

    @NotBlank(message = "Delivery location is required")
    @Size(max = 100)
    private String deliveryLocation;

    @NotBlank(message = "Delivery method is required")
    @Size(max = 50)
    private String deliveryMethod;

    private boolean willingToShip;

    @NotBlank(message = "Price model is required")
    @Size(max = 50)
    private String priceModel;

    private boolean photosRequired;
    private boolean visitFarm;


    @AssertTrue(message = "Delivery date must be after harvest date")
    public boolean isDeliveryDateValid() {
        if (harvestDate == null || deliveryDate == null) {
            return true;
        }
        return deliveryDate.isAfter(harvestDate) ||
                (flexibleDates && !deliveryDate.isBefore(harvestDate));
    }
}
