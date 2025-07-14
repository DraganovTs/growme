package com.home.preorder.service.model.dto;

import com.home.preorder.service.model.enums.BidStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateBidStatusRequestDTO(
        @NotNull BidStatus bidStatus,
        @NotNull UUID userId
) {
}
