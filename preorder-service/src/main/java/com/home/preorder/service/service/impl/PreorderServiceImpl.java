package com.home.preorder.service.service.impl;

import com.home.preorder.service.model.dto.*;
import com.home.preorder.service.service.*;
import com.home.preorder.service.specification.TaskSpecParams;
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

    //BID

    @Override
    public BidResponseDTO requestCreateBid(CreateBidRequestDTO dto) {
        return bidCommandService.createBid(dto);
    }

    @Override
    public void requestWithdrawBid(UUID bidId, UUID userId) {

    }

    @Override
    public BidResponseDTO requestBidById(UUID bidId) {
        return bidQueryService.getBidById(bidId);
    }

    @Override
    public List<BidResponseDTO> requestBidsForTask(UUID taskId) {
        return bidQueryService.getBidsByTaskId(taskId);
    }

    @Override
    public List<BidResponseDTO> requestUserBids(UUID userId) {
        return List.of();
    }

    @Override
    public BidResponseDTO requestCounterOffer(UUID bidId, CounterOfferRequestDTO dto, UUID userId) {
        return null;
    }

    @Override
    public BidResponseDTO requestUpdateBidStatus(UUID bidId, UpdateBidStatusRequestDTO dto) {
        return null;
    }


}
