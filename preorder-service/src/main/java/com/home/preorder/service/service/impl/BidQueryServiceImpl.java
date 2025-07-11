package com.home.preorder.service.service.impl;

import com.home.preorder.service.mapper.BidMapper;
import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.repository.BidRepository;
import com.home.preorder.service.service.BidQueryService;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "taskBids" , key = "#taskId")
    public List<BidResponseDTO> getBidsByTaskId(UUID taskId) {

        return bidRepository.findAllByTaskId(taskId)
                .stream()
                .map(bidMapper::mapBidToBidResponseDTO)
                .toList();

    }
}
