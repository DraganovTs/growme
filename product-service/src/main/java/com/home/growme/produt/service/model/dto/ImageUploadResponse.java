package com.home.growme.produt.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageUploadResponse {
    private String filename;
    private String originalName;
    private String url;
}
