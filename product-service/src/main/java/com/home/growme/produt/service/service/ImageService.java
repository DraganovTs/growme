package com.home.growme.produt.service.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    String saveImage(MultipartFile file);

    List<String> getAllImages();
}
