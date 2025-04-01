package com.home.growme.produt.service.service.impl;

import com.home.growme.produt.service.model.dto.ImageMetadataDTO;
import com.home.growme.produt.service.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    @Override
    public String uploadImage(MultipartFile file) {
        return "";
    }

    @Override
    public List<String> getAllImages() {
        return List.of();
    }

    @Override
    public void deleteImage(String imageUrl) {

    }

    @Override
    public ImageMetadataDTO getImageMetadata(String imageUrl) {
        return null;
    }
}
