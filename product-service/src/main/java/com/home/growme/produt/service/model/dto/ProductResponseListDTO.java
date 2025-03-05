package com.home.growme.produt.service.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductResponseListDTO {

    private int totalPages;
    private long totalCount;
    private int pageIndex;
    private int pageSize;
    private List<ProductResponseDTO> dataList;



}
