package com.home.growme.produt.service.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CategoryDTO {

    private UUID categoryId;
    private String categoryName;
}
