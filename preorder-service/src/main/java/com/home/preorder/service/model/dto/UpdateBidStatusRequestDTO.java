package com.home.preorder.service.model.dto;

import com.home.preorder.service.model.enums.BidStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateBidStatusRequestDTO(
        @NotNull BidStatus bidStatus
) {
}
