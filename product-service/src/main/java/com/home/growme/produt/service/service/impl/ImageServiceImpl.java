package com.home.growme.produt.service.service.impl;

import com.home.growme.produt.service.model.dto.ImageMetadataDTO;
import com.home.growme.produt.service.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
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

@Service
public class ImageServiceImpl implements ImageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String simpleName = originalFilename.substring(0, originalFilename.lastIndexOf("."))

                    .replaceAll("[^a-zA-Z0-9]", "_");
            String uniqueFilename = simpleName + "_" + System.currentTimeMillis() + fileExtension;

            Path targetLocation = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file: " + ex.getMessage());
        }
    }

    @Override
    public List<String> getAllImages() {
        try (Stream<Path> paths = Files.walk(Paths.get(uploadDir), 1)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path file = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    @Override
    public ImageMetadataDTO getImageMetadata(String imageUrl) {
        try {
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path file = Paths.get(uploadDir).resolve(filename);

            return new ImageMetadataDTO(
                    imageUrl,
                    filename,
                    Files.size(file),
                    Files.probeContentType(file)
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to get image metadata", e);
        }
    }
}

