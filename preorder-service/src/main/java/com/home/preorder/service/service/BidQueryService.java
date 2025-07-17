package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.BidResponseListDTO;
import com.home.preorder.service.specification.BidSpecParams;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BidQueryService {

    BidResponseListDTO getBidsByTaskId(UUID taskId, BidSpecParams request);

    BidResponseDTO getBidById(UUID bidId);

    BidResponseListDTO getUserBids(UUID userId,  BidSpecParams request);

    BidResponseListDTO getBidsRequiringAction(UUID userId,  BidSpecParams request);
}
