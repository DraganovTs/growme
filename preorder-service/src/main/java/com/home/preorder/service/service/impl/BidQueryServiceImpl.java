package com.home.preorder.service.service.impl;

import com.home.preorder.service.exception.BidNotFoundException;
import com.home.preorder.service.mapper.BidMapper;
import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.BidResponseListDTO;
import com.home.preorder.service.model.entity.Bid;
import com.home.preorder.service.model.enums.BidStatus;
import com.home.preorder.service.repository.BidRepository;
import com.home.preorder.service.service.BidQueryService;
import com.home.preorder.service.specification.BidSpecParams;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BidQueryServiceImpl implements BidQueryService {

    private final BidRepository bidRepository;
    private final BidMapper bidMapper;

    public BidQueryServiceImpl(BidRepository bidRepository, BidMapper bidMapper) {
        this.bidRepository = bidRepository;
        this.bidMapper = bidMapper;
    }

    public BidResponseListDTO getBidsByTaskId(UUID taskId, BidSpecParams request) {
        Pageable pageable = PageRequest.of(
                request.getPageIndex() - 1,
                request.getPageSize(),
                getSort(request.getSort())
        );

        Page<Bid> bids = bidRepository.findAllByTaskId(taskId, pageable);

        return BidResponseListDTO.builder()
                .dataList(bids.getContent().stream().map(bidMapper::mapBidToBidResponseDTO)
                        .collect(Collectors.toList()))
                .totalPages(bids.getTotalPages())
                .totalCount(bids.getTotalElements())
                .pageIndex(bids.getNumber() + 1)
                .pageSize(bids.getSize())
                .build();
    }

    @Override
    public BidResponseDTO getBidById(UUID bidId) {
        return bidMapper.mapBidToBidResponseDTO(bidRepository.findById(bidId)
                .orElseThrow(() -> new BidNotFoundException("Bid whit not found whit id: " + bidId)));
    }

    @Override
    public BidResponseListDTO getUserBids(UUID userId, BidSpecParams request) {
        Pageable pageable = PageRequest.of(
                request.getPageIndex() - 1,
                request.getPageSize(),
                getSort(request.getSort())
        );

        Page<Bid> bids = bidRepository.findAllByUserId(userId, pageable);

        return BidResponseListDTO.builder()
                .dataList(bids.getContent().stream().map(bidMapper::mapBidToBidResponseDTO)
                        .collect(Collectors.toList()))
                .totalPages(bids.getTotalPages())
                .totalCount(bids.getTotalElements())
                .pageIndex(bids.getNumber() + 1)
                .pageSize(bids.getSize())
                .build();
    }

    @Override
    public BidResponseListDTO getBidsRequiringAction(UUID userId, BidSpecParams request) {
        Pageable pageable = PageRequest.of(
                request.getPageIndex() - 1,
                request.getPageSize(),
                getSort(request.getSort())
        );

        Page<Bid> page = bidRepository.findAllByTaskUserIdAndStatusIn(
                userId,
                List.of(BidStatus.PENDING, BidStatus.COUNTER_OFFER),
                pageable
        );

        return BidResponseListDTO.builder()
                .dataList(page.getContent().stream().map(bidMapper::mapBidToBidResponseDTO)
                        .collect(Collectors.toList()))
                .totalPages(page.getTotalPages())
                .totalCount(page.getTotalElements())
                .pageIndex(page.getNumber() + 1)
                .pageSize(page.getSize())
                .build();
    }

    private Sort getSort(String sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted();
        }

        String[] parts = sort.split("(?=[A-Z])");
        if (parts.length != 2) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String property = parts[0].toLowerCase();
        Sort.Direction direction = parts[1].equalsIgnoreCase("Desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        return Sort.by(direction, property);
    }


}
