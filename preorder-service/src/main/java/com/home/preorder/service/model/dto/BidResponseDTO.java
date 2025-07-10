package com.home.preorder.service.model.dto;

import com.home.preorder.service.model.enums.BidStatus;
import com.home.preorder.service.model.enums.DeliveryMethod;
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
    private BigDecimal price;
    private String message;
    private BidStatus status;
    private UUID taskId;
    private String taskTitle;
    private UUID growerId;
    private String growerName;
    private LocalDate proposedHarvestDate;
    private DeliveryMethod deliveryMethod;
    private boolean deliveryIncluded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
