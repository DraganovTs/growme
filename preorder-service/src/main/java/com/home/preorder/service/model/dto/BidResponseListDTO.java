package com.home.preorder.service.model.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BidResponseListDTO {
    private int totalPages;
    private long totalCount;
    private int pageIndex;
    private int pageSize;
    private List<BidResponseDTO> dataList;
}
