package com.home.preorder.service.service.impl;

import com.home.preorder.service.exception.BidNotFoundException;
import com.home.preorder.service.mapper.BidMapper;
import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.enums.BidStatus;
import com.home.preorder.service.repository.BidRepository;
import com.home.preorder.service.service.BidQueryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BidQueryServiceImpl implements BidQueryService {

    private final BidRepository bidRepository;
    private final BidMapper bidMapper;

    public BidQueryServiceImpl(BidRepository bidRepository, BidMapper bidMapper) {
        this.bidRepository = bidRepository;
        this.bidMapper = bidMapper;
    }

    @Override
    @Cacheable(value = "taskBids", key = "#taskId")
    public Page<BidResponseDTO> getBidsByTaskId(UUID taskId, Pageable pageable) {

        return bidRepository.findAllByTaskId(taskId, pageable)
                .stream()
                .map(bidMapper::mapBidToBidResponseDTO);

    }

    @Override
    public BidResponseDTO getBidById(UUID bidId) {
        return bidMapper.mapBidToBidResponseDTO(bidRepository.findById(bidId)
                .orElseThrow(() -> new BidNotFoundException("Bid whit not found whit id: " + bidId)));
    }

    @Override
    public Page<BidResponseDTO> getUserBids(UUID userId, Pageable pageable) {
        return bidRepository.findAllByUserId(userId, pageable)
                .map(bidMapper::mapBidToBidResponseDTO);
    }

    @Override
    public Page<BidResponseDTO> getBidsRequiringAction(UUID userId, Pageable pageable) {
        return bidRepository.findAllByTaskUserIdAndStatusIn(
                userId,
                List.of(BidStatus.PENDING, BidStatus.COUNTER_OFFER),
                pageable
        ).map(bidMapper::mapBidToBidResponseDTO);
    }
}
