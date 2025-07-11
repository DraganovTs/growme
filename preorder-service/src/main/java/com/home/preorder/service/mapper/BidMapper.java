package com.home.preorder.service.mapper;

import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.CreateBidRequestDTO;
import com.home.preorder.service.model.entity.Bid;
import com.home.preorder.service.model.enums.BidStatus;
import com.home.preorder.service.model.enums.DeliveryMethod;
import org.springframework.stereotype.Component;


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
                .taskId(save.getTask().getTaskId())
                .price(save.getPrice())
                .message(save.getMessage())
                .proposedHarvestDate(save.getProposedHarvestDate())
                .deliveryMethod(save.getDeliveryMethod().toString())
                .status(save.getStatus().toString())
                .taskTitle(save.getTask().getTitle())
                .userId(save.getUserId())
                .userName(save.getUserName())
                .deliveryIncluded(save.isDeliveryIncluded())
                .createdAt(save.getCreatedAt())
                .updatedAt(save.getUpdatedAt())
                .build();
    }
}
