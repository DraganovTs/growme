package com.home.preorder.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CategoryDTO {

    private UUID categoryId;

    @NotBlank
    @Size(max = 30)
    private String categoryName;
}
