package com.home.preorder.service.service.impl;

import com.home.preorder.service.exception.BidNotFoundException;
import com.home.preorder.service.mapper.BidMapper;
import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.BidResponseListDTO;
import com.home.preorder.service.model.entity.Bid;
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
    @Cacheable(value = "taskBids", key = "#taskId.toString().concat('-').concat(#pageable.pageNumber)")
    public BidResponseListDTO getBidsByTaskId(UUID taskId, Pageable pageable) {

       Page<Bid> bids = bidRepository.findAllByTaskId(taskId, pageable);

         return BidResponseListDTO.builder()
                 .totalPages(pageable.getPageSize())

                 .build();

         //TODO
    }

    @Override
    public BidResponseDTO getBidById(UUID bidId) {
        return bidMapper.mapBidToBidResponseDTO(bidRepository.findById(bidId)
                .orElseThrow(() -> new BidNotFoundException("Bid whit not found whit id: " + bidId)));
    }

    @Override
    public BidResponseListDTO getUserBids(UUID userId, Pageable pageable) {
         Page<Bid> bids = bidRepository.findAllByUserId(userId, pageable);
         return null;
        //TODO

    }

    @Override
    public BidResponseListDTO getBidsRequiringAction(UUID userId, Pageable pageable) {
        Page<Bid> bids = bidRepository.findAllByTaskUserIdAndStatusIn(
                userId,
                List.of(BidStatus.PENDING, BidStatus.COUNTER_OFFER),
                pageable
        );

        return null;
        //TODO
    }
}
