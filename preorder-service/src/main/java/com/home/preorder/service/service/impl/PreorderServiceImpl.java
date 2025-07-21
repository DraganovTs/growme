package com.home.preorder.service.service.impl;

import com.home.preorder.service.model.dto.*;
import com.home.preorder.service.service.*;
import com.home.preorder.service.specification.BidSpecParams;
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
    private final EmailService emailService;

    public PreorderServiceImpl(TaskCommandService taskCommandService, TaskQueryService taskQueryService,
                               BidCommandService bidCommandService, BidQueryService bidQueryService, EmailService emailService) {
        this.taskCommandService = taskCommandService;
        this.taskQueryService = taskQueryService;
        this.bidCommandService = bidCommandService;
        this.bidQueryService = bidQueryService;
        this.emailService = emailService;
    }


    @Override
    public TaskDTO requestTaskCreation(TaskDTO taskDTO) {
        TaskDTO createdTask = taskCommandService.createTask(taskDTO);
        emailService.sendTaskCreationConfirmation(taskDTO.getUserId());
        return createdTask;
    }

    @Override
    public TaskDTO requestUpdateTaskStatus(String taskId, TaskStatusUpdateRequestDTO taskStatus) {
        TaskDTO updatedTask = taskCommandService.updateTaskStatus(taskId, taskStatus);
        emailService.sendTaskStatusUpdateNotification(updatedTask.getUserId());
        return updatedTask;
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
    public void requestCancelTask(UUID taskId, UUID userId) {
        taskCommandService.cancelTask(taskId, userId);
        emailService.sendTaskCancellationConfirmation(userId);
    }

    //BID

    @Override
    public BidResponseDTO requestCreateBid(CreateBidRequestDTO dto) {
        BidResponseDTO createdBid = bidCommandService.createBid(dto);
        emailService.sendBidCreationConfirmation(dto.getUserId());
        return createdBid;
    }

    @Override
    public void requestWithdrawBid(UUID bidId, UUID userId) {
        bidCommandService.withdrawBid(bidId, userId);
        emailService.sendBidWithdrawalConfirmation(userId);
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
    public BidResponseListDTO requestUserBids(UUID userId, BidSpecParams request) {
        return bidQueryService.getUserBids(userId, request);
    }

    @Override
    public BidResponseDTO requestCounterOffer(UUID bidId, CounterOfferRequestDTO dto, UUID userId) {
        BidResponseDTO counterOffer = bidCommandService.createCounterOffer(bidId, dto, userId);
        emailService.sendCounterOfferNotification(userId);
        return counterOffer;
    }

    @Override
    public BidResponseDTO requestUpdateBidStatus(UUID bidId, UpdateBidStatusRequestDTO dto) {
        BidResponseDTO updatedBid = bidCommandService.updateBidStatus(bidId, dto);
        emailService.sendBidStatusUpdateNotification(dto.userId());
        return updatedBid;
    }

    @Override
    public BidResponseListDTO requestBidsRequiringAction(UUID userId, BidSpecParams request) {
        BidResponseListDTO bidRequiringAction = bidQueryService.getBidsRequiringAction(userId, request);
        emailService.sendActionRequiredNotification(userId);
        return bidRequiringAction;
    }


}
