package com.home.growme.produt.service.service.impl;

import com.home.growme.produt.service.exception.FileStorageException;
import com.home.growme.produt.service.model.dto.ImageDisplayDTO;
import com.home.growme.produt.service.model.dto.ImageMetadataDTO;
import com.home.growme.produt.service.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private static final int MAX_RECENT_IMAGES = 5;
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("_\\d+$");
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // 1. Prepare upload directory
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Generate safe filename
            String filename = generateUniqueFilename(file.getOriginalFilename());

            // 3. Store file
            Files.copy(file.getInputStream(),
                    uploadPath.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);

            // 4. Return just the filename
            return filename;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file: " + ex.getMessage());
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
    public List<ImageDisplayDTO> getRecentImagesForDisplay() {
        try (Stream<Path> paths = Files.walk(Paths.get(uploadDir), 1)) {
            return paths
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(this::getLastModifiedTime).reversed())
                    .limit(5)
                    .map(this::mapToDisplayDTO)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }
    }

    private ImageDisplayDTO mapToDisplayDTO(Path filePath) {
        String filename = filePath.getFileName().toString();
        return new ImageDisplayDTO(
                filename,
                getCleanDisplayName(filename),
                "http://localhost:8087/api/products/images/" + filename
        );
    }

    private String getCleanDisplayName(String filename) {
        String nameWithoutExt = filename.replaceFirst("[.][^.]+$", "");
        return TIMESTAMP_PATTERN.matcher(nameWithoutExt).replaceAll("");
    }


    private long getLastModifiedTime(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            return 0;
        }
    }

    private String generateUniqueFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "product_" + System.currentTimeMillis() + ".jpg";
        }

        // Keep only safe characters
        String safeName = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");

        // Add timestamp before extension
        int dotIndex = safeName.lastIndexOf('.');
        String baseName = dotIndex > 0 ? safeName.substring(0, dotIndex) : safeName;
        String extension = dotIndex > 0 ? safeName.substring(dotIndex) : ".jpg";

        return baseName + "_" + System.currentTimeMillis() + extension;
    }
}

