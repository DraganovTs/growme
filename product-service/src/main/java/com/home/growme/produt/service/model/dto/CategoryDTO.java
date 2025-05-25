package com.home.growme.produt.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CategoryDTO {

    private UUID categoryId;

    @Size(min = 3, max = 10)
    @NotBlank(message = "Category name is required")
    private String categoryName;
}
