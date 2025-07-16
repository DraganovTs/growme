package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.BidResponseListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BidQueryService {

    BidResponseListDTO getBidsByTaskId(UUID taskId, Pageable pageable);

    BidResponseDTO getBidById(UUID bidId);

    BidResponseListDTO getUserBids(UUID userId, Pageable pageable);

    BidResponseListDTO getBidsRequiringAction(UUID userId, Pageable pageable);
}
