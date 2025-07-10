package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.*;
import com.home.preorder.service.specification.TaskSpecParams;

import java.util.List;
import java.util.UUID;

public interface PreorderService {

    //Task
    TaskDTO requestTaskCreation(TaskDTO taskDTO);

    TaskDTO  requestUpdateTaskStatus(String taskId, TaskStatusUpdateRequestDTO taskStatus);

    TaskDTO requestTaskById(String taskId);

    List<TaskDTO> requestTaskByUser(TaskSpecParams request);

    TaskResponseListDTO requestAllTasks(TaskSpecParams request);

    //Bid
    BidResponseDTO requestCreateBid(CreateBidRequestDTO dto, UUID userId);
    void requestWithdrawBid(UUID bidId, UUID userId);
    BidResponseDTO requestBidById(UUID bidId);
    List<BidResponseDTO> requestBidsForTask(UUID taskId);
    List<BidResponseDTO> requestUserBids(UUID userId);
    BidResponseDTO requestCounterOffer(UUID bidId, CounterOfferRequestDTO dto, UUID userId);
    BidResponseDTO requestUpdateBidStatus(UUID bidId, UpdateBidStatusRequestDTO dto);
}
