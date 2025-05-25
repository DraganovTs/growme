package com.home.growme.produt.service.service;

import com.home.growme.produt.service.model.dto.ImageDisplayDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing image operations, such as uploading, retrieving,
 * and displaying images in the product service.
 */
public interface ImageService {
    String uploadImage(MultipartFile file);
    List<String> getAllImages();
    List<ImageDisplayDTO> getRecentImagesForDisplay();

}
