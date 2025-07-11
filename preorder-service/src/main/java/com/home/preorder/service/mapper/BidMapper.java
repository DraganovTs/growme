package com.home.preorder.service.mapper;

import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.CreateBidRequestDTO;
import com.home.preorder.service.model.entity.Bid;
import com.home.preorder.service.model.enums.BidStatus;
import com.home.preorder.service.model.enums.DeliveryMethod;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BidMapper {


    public Bid mapCreateBidReuqestDTOToBid(CreateBidRequestDTO dto) {
        return Bid.builder()
                .price(dto.getPrice())
                .message(dto.getMessage())
                .proposedHarvestDate(dto.getProposedHarvestDate())
                .deliveryMethod(DeliveryMethod.valueOf(dto.getDeliveryMethod().toString()))
                .userId(dto.getUserId())
                .deliveryIncluded(dto.isDeliveryIncluded())
                .status(BidStatus.valueOf(dto.getStatus().toString()))
                .userName(dto.getUserName())
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
