package com.home.preorder.service.service.impl;

import com.home.preorder.service.exception.TaskNotFoundException;
import com.home.preorder.service.mapper.BidMapper;
import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.CreateBidRequestDTO;
import com.home.preorder.service.model.entity.Bid;
import com.home.preorder.service.model.entity.Task;
import com.home.preorder.service.repository.BidRepository;
import com.home.preorder.service.repository.TaskRepository;
import com.home.preorder.service.service.BidCommandService;
import org.springframework.stereotype.Service;

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
    public BidResponseDTO createBid(CreateBidRequestDTO dto, UUID userId) {

        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException("Task not found whit Id: " + dto.getTaskId()));

        Bid bid = bidMapper.mapCreateBidReuqestDTOToBid(dto , userId);
        bid.setTask(task);


        return bidMapper.mapBidToBidResponseDTO(bidRepository.save(bid));
    }
}
