package com.home.preorder.service.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BidResponseDTO {
    private UUID bidId;
    private UUID taskId;
    private BigDecimal price;
    private String message;
    private LocalDate proposedHarvestDate;
    private String deliveryMethod;
    private String status;
    private String taskTitle;
    private UUID userId;
    private String userName;
    private boolean deliveryIncluded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
