package com.home.growme.produt.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageDisplayDTO {
    private String filename;
    private String displayName;
    private String url;
}
