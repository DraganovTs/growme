package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.*;
import com.home.preorder.service.specification.BidSpecParams;
import com.home.preorder.service.specification.TaskSpecParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PreorderService {

    //Task
    TaskDTO requestTaskCreation(TaskDTO taskDTO);

    TaskDTO requestUpdateTaskStatus(String taskId, TaskStatusUpdateRequestDTO taskStatus);

    TaskDTO requestTaskById(String taskId);

    List<TaskDTO> requestTaskByUser(TaskSpecParams request);

    TaskResponseListDTO requestAllTasks(TaskSpecParams request);

    void requestCancelTask(UUID taskId, UUID userId);

    //Bid
    BidResponseDTO requestCreateBid(CreateBidRequestDTO dto);

    void requestWithdrawBid(UUID bidId, UUID userId);

    BidResponseDTO requestBidById(UUID bidId);

    BidResponseListDTO requestBidsForTask(UUID taskId, BidSpecParams request);

    BidResponseListDTO requestUserBids(UUID userId,  BidSpecParams request);

    BidResponseDTO requestCounterOffer(UUID bidId, CounterOfferRequestDTO dto, UUID userId);

    BidResponseDTO requestUpdateBidStatus(UUID bidId, UpdateBidStatusRequestDTO dto);

    BidResponseListDTO requestBidsRequiringAction(UUID userId,  BidSpecParams request);

}
