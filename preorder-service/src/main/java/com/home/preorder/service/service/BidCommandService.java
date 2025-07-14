package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.CounterOfferRequestDTO;
import com.home.preorder.service.model.dto.CreateBidRequestDTO;
import com.home.preorder.service.model.dto.UpdateBidStatusRequestDTO;

import java.util.UUID;

public interface BidCommandService {
    BidResponseDTO createBid(CreateBidRequestDTO dto);
    void withdrawBid(UUID bidId, UUID userId);
    BidResponseDTO createCounterOffer(UUID bidId, CounterOfferRequestDTO dto, UUID userId);
    BidResponseDTO updateBidStatus(UUID bidId, UpdateBidStatusRequestDTO dto);
}
