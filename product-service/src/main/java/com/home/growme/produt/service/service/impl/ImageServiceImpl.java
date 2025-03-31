package com.home.growme.produt.service.service.impl;

import com.home.growme.produt.service.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private static final String UPLOAD_DIR = "uploads/";

    public ImageServiceImpl() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            log.error("Could not create upload directory!", e);
        }
    }


    @Override
    public String saveImage(MultipartFile file) {
        try {
            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            log.info("Image saved: {}", filePath);
            return "/uploads/" + file.getOriginalFilename();
        } catch (IOException e) {
            log.error("Error saving image", e);
            throw new RuntimeException("Error saving image", e);
        }
    }

    @Override
    public List<String> getAllImages() {
        try (Stream<Path> paths = Files.walk(Paths.get(UPLOAD_DIR), 1)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> "/uploads/" + path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error retrieving images", e);
            throw new RuntimeException("Error retrieving images", e);
        }
    }
    }

