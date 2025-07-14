package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.BidResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BidQueryService {

    Page<BidResponseDTO> getBidsByTaskId(UUID taskId, Pageable pageable);

    BidResponseDTO getBidById(UUID bidId);

    Page<BidResponseDTO> getUserBids(UUID userId, Pageable pageable);

    Page<BidResponseDTO> getBidsRequiringAction(UUID userId, Pageable pageable);
}
