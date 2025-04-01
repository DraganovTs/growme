package com.home.growme.produt.service.service;

import com.home.growme.produt.service.model.dto.ImageMetadataDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    String uploadImage(MultipartFile file);
    List<String> getAllImages();
    void deleteImage(String imageUrl);
    ImageMetadataDTO getImageMetadata(String imageUrl);
}
