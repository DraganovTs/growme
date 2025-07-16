package com.home.growme.produt.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
/**
 * Represents a paginated list of product responses.
 */
@Schema(name = "ProductResponseList", description = "Paginated product list response")
@Data
@Builder
public class ProductResponseListDTO  {
    @Schema(description = "Total number of pages", example = "5")
    private int totalPages;
    @Schema(description = "Total number of elements", example = "42")
    private long totalCount;
    @Schema(description = "Current page index", example = "0")
    private int pageIndex;
    @Schema(description = "Size of the page", example = "10")
    private int pageSize;
    @Schema(description = "List of product DTOs")
    private List<ProductResponseDTO> dataList;



}
