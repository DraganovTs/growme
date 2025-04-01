package com.home.growme.produt.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageMetadataDTO {

    private String url;
    private String filename;
    private long size;
    private String contentType;
}
