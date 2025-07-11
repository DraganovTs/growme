package com.home.preorder.service.service;

import com.home.preorder.service.model.dto.BidResponseDTO;

import java.util.List;
import java.util.UUID;

public interface BidQueryService {

    List<BidResponseDTO> getBidsByTaskId(UUID taskId);
}
