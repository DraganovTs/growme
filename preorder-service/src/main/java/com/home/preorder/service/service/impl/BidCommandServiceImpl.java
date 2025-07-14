package com.home.preorder.service.service.impl;

import com.home.preorder.service.exception.BidNotFoundException;
import com.home.preorder.service.exception.InvalidBidStatusException;
import com.home.preorder.service.exception.TaskNotFoundException;
import com.home.preorder.service.exception.UnauthorizedActionException;
import com.home.preorder.service.mapper.BidMapper;
import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.CounterOfferRequestDTO;
import com.home.preorder.service.model.dto.CreateBidRequestDTO;
import com.home.preorder.service.model.dto.UpdateBidStatusRequestDTO;
import com.home.preorder.service.model.entity.Bid;
import com.home.preorder.service.model.entity.Task;
import com.home.preorder.service.model.enums.BidStatus;
import com.home.preorder.service.repository.BidRepository;
import com.home.preorder.service.repository.TaskRepository;
import com.home.preorder.service.service.BidCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class BidCommandServiceImpl implements BidCommandService {

    private final TaskRepository taskRepository;
    private final BidMapper bidMapper;
    private final BidRepository bidRepository;

    public BidCommandServiceImpl(TaskRepository taskRepository, BidMapper bidMapper, BidRepository bidRepository) {
        this.taskRepository = taskRepository;
        this.bidMapper = bidMapper;
        this.bidRepository = bidRepository;
    }


    @Override
    public BidResponseDTO createBid(CreateBidRequestDTO dto) {

        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException("Task not found whit Id: " + dto.getTaskId()));

        Bid bid = bidMapper.mapCreateBidReuqestDTOToBid(dto);
        bid.setTask(task);


        return bidMapper.mapBidToBidResponseDTO(bidRepository.save(bid));
    }

    @Override
    @Transactional
    public void withdrawBid(UUID bidId, UUID userId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new BidNotFoundException("Bid not found with id: " + bidId));

        if (bid.getUserId().equals(userId)) {
            throw new UnauthorizedActionException("User is not authorized to withdraw this bid");
        }

        if (bid.getStatus() != BidStatus.PENDING && bid.getStatus() != BidStatus.COUNTER_OFFER) {
            throw new InvalidBidStatusException("Bid cannot be withdrawn in its current status");
        }

        bidRepository.delete(bid);
    }

    @Override
    @Transactional
    public BidResponseDTO createCounterOffer(UUID bidId, CounterOfferRequestDTO dto, UUID userId) {

        Bid originalBid = bidRepository.findById(bidId)
                .orElseThrow(() -> new BidNotFoundException("Bid not found with id: " + bidId));

        if (originalBid.getTask().getUserId().equals(userId)) {
            throw new UnauthorizedActionException("User is not authorized to create counter offer for this bid");
        }

        if (originalBid.getStatus() != BidStatus.PENDING) {
            throw new InvalidBidStatusException("Counter offer can only be made for pending bids");
        }

        originalBid.setStatus(BidStatus.COUNTER_OFFER);
        originalBid.setCounterOfferPrice(dto.counterOfferPrice());
        originalBid.setCounterOfferMessage(dto.message());

        return bidMapper.mapBidToBidResponseDTO(bidRepository.save(originalBid));
    }

    @Override
    @Transactional
    public BidResponseDTO updateBidStatus(UUID bidId, UpdateBidStatusRequestDTO dto) {

        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new BidNotFoundException("Bid not found with id: " + bidId));

        if (bid.getTask().getUserId().equals(dto.userId())) {
            throw new UnauthorizedActionException("User is not authorized to update this bid's status");
        }
        bid.setStatus(dto.bidStatus());
        return bidMapper.mapBidToBidResponseDTO(bidRepository.save(bid));
    }
}
