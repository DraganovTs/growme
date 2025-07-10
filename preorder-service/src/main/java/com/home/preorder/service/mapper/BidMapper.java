package com.home.preorder.service.mapper;

import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.CreateBidRequestDTO;
import com.home.preorder.service.model.entity.Bid;
import com.home.preorder.service.model.enums.BidStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BidMapper {


    public Bid mapCreateBidReuqestDTOToBid(CreateBidRequestDTO dto, UUID userId) {
        return Bid.builder()
                .price(dto.getPrice())
                .message(dto.getMessage())
                .status(BidStatus.IN_PROGRESS)
                .userId(userId)
                .build();
    }

    public BidResponseDTO mapBidToBidResponseDTO(Bid save) {
        return BidResponseDTO.builder()
                .bidId(save.getBidId())
                .price(save.getPrice())
                .message(save.getMessage())
                .status(save.getStatus())
                .taskId(save.getTask().getTaskId())
                .taskTitle(save.getTask().getTitle())
                .growerId(save.getUserId())
                .growerName("Test Name to add")
                .build();
    }
}
