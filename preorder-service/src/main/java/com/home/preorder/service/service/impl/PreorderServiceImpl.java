package com.home.preorder.service.service.impl;

import com.home.preorder.service.model.dto.*;
import com.home.preorder.service.service.*;
import com.home.preorder.service.specification.BidSpecParams;
import com.home.preorder.service.specification.TaskSpecParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PreorderServiceImpl implements PreorderService {

    private final TaskCommandService taskCommandService;
    private final TaskQueryService taskQueryService;
    private final BidCommandService bidCommandService;
    private final BidQueryService bidQueryService;

    public PreorderServiceImpl(TaskCommandService taskCommandService, TaskQueryService taskQueryService,
                               BidCommandService bidCommandService, BidQueryService bidQueryService) {
        this.taskCommandService = taskCommandService;
        this.taskQueryService = taskQueryService;
        this.bidCommandService = bidCommandService;
        this.bidQueryService = bidQueryService;
    }


    @Override
    public TaskDTO requestTaskCreation(TaskDTO taskDTO) {
        return taskCommandService.createTask(taskDTO);
    }

    @Override
    public TaskDTO requestUpdateTaskStatus(String taskId, TaskStatusUpdateRequestDTO taskStatus) {
        return taskCommandService.updateTaskStatus(taskId, taskStatus);
    }

    @Override
    public TaskDTO requestTaskById(String taskId) {
        return taskQueryService.getTaskById(taskId);
    }

    @Override
    public List<TaskDTO> requestTaskByUser(TaskSpecParams request) {
        return taskQueryService.getTasksByUser(request);
    }

    @Override
    public TaskResponseListDTO requestAllTasks(TaskSpecParams request) {
        return taskQueryService.getAllTasks(request);
    }

    @Override
    public void requestCancelTask(UUID taskId , UUID userId) {
        taskCommandService.cancelTask(taskId , userId);
    }

    //BID

    @Override
    public BidResponseDTO requestCreateBid(CreateBidRequestDTO dto) {
        return bidCommandService.createBid(dto);
    }

    @Override
    public void requestWithdrawBid(UUID bidId, UUID userId) {
        bidCommandService.withdrawBid(bidId, userId);
    }

    @Override
    public BidResponseDTO requestBidById(UUID bidId) {
        return bidQueryService.getBidById(bidId);
    }

    @Override
    public BidResponseListDTO requestBidsForTask(UUID taskId, BidSpecParams request) {
        return bidQueryService.getBidsByTaskId(taskId, request);
    }

    @Override
    public BidResponseListDTO requestUserBids(UUID userId,  BidSpecParams request) {
        return bidQueryService.getUserBids(userId, request);
    }

    @Override
    public BidResponseDTO requestCounterOffer(UUID bidId, CounterOfferRequestDTO dto, UUID userId) {
        return bidCommandService.createCounterOffer(bidId, dto, userId);
    }

    @Override
    public BidResponseDTO requestUpdateBidStatus(UUID bidId, UpdateBidStatusRequestDTO dto) {
        return bidCommandService.updateBidStatus(bidId, dto);
    }

    @Override
    public BidResponseListDTO requestBidsRequiringAction(UUID userId,  BidSpecParams request) {
        return bidQueryService.getBidsRequiringAction(userId, request);
    }


}
