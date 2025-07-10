package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.CreateBidRequestDTO;

import java.util.UUID;

public interface BidCommandService {
    BidResponseDTO createBid(CreateBidRequestDTO dto, UUID userId);
}
