package com.home.growme.product.service.service.impl;
import com.home.growme.product.service.exception.FileStorageException;
import com.home.growme.product.service.model.dto.ImageDisplayDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class ImageServiceImplTests {
    @Mock
    private Environment environment;

    private ImageServiceImpl imageService;

    private final String testUploadDir = "test-uploads";  // Define a constant test directory

    private final String testImageName = "test_image.jpg";

    @BeforeEach
    void setUp() throws IOException {
        // Initialize the service manually with the test directory
        imageService = new ImageServiceImpl();
        // Use reflection to set the upload directory
        ReflectionTestUtils.setField(imageService, "uploadDir", testUploadDir);

        Path uploadPath = Paths.get(testUploadDir);
        if (Files.exists(uploadPath)) {
            Files.walk(uploadPath)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        Files.createDirectories(uploadPath);
    }


    @AfterEach
    void tearDown() throws IOException {
        // Clean up after tests
        Path uploadPath = Paths.get(testUploadDir);
        if (Files.exists(uploadPath)) {
            Files.walk(uploadPath)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    // Helper method to create test files
    private void createTestFiles(String... filenames) throws IOException {
        for (String filename : filenames) {
            Path filePath = Paths.get(testUploadDir, filename);
            Files.createFile(filePath);
            // Set different modified times for sorting tests
            Files.setLastModifiedTime(filePath,
                    FileTime.from(Instant.now().minusSeconds(filename.length())));
        }
    }

    @Test
    void uploadImage_shouldStoreFile_whenValidFileProvided() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                testImageName,
                "image/jpeg",
                "test image content".getBytes()
        );

        // Act
        String result = imageService.uploadImage(file);

        // Assert
        assertNotNull(result);
        assertTrue(Files.exists(Paths.get(testUploadDir, result)));
    }

    @Test
    void uploadImage_shouldGenerateUniqueName_whenFilenameEmpty() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Act
        String result = imageService.uploadImage(file);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("product_"));
        assertTrue(result.endsWith(".jpg"));
        assertTrue(Files.exists(Paths.get(testUploadDir, result)));
    }

    @Test
    void uploadImage_shouldSanitizeFilename() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "invalid@name#.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Act
        String result = imageService.uploadImage(file);

        // Assert
        assertNotNull(result);
        assertFalse(result.contains("@"));
        assertFalse(result.contains("#"));
        assertTrue(result.endsWith(".jpg"));
    }

    @Test
    void uploadImage_shouldThrowException_whenIOExceptionOccurs() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(testImageName);
        when(file.getInputStream()).thenThrow(new IOException("Test exception"));

        // Act & Assert
        assertThrows(FileStorageException.class, () -> imageService.uploadImage(file));
    }

    @Test
    void getAllImages_shouldReturnEmptyList_whenNoFilesExist() {
        // Act
        List<String> result = imageService.getAllImages();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllImages_shouldReturnAllFiles_whenFilesExist() throws IOException {
        // Arrange
        createTestFiles("image1.jpg", "image2.png", "document.pdf");

        // Act
        List<String> result = imageService.getAllImages();

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains("image1.jpg"));
        assertTrue(result.contains("image2.png"));
        assertTrue(result.contains("document.pdf"));
    }


    @Test
    void getRecentImagesForDisplay_shouldReturnEmptyList_whenNoFilesExist() {
        // Act
        List<ImageDisplayDTO> result = imageService.getRecentImagesForDisplay();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRecentImagesForDisplay_shouldReturnLimitedResults() throws IOException {
        // Arrange
        createTestFiles("image1_123.jpg", "image2_456.jpg", "image3_789.jpg",
                "image4_012.jpg", "image5_345.jpg", "image6_678.jpg");

        // Act
        List<ImageDisplayDTO> result = imageService.getRecentImagesForDisplay();

        // Assert
        assertEquals(5, result.size());
    }

    @Test
    void getRecentImagesForDisplay_shouldReturnMostRecentFiles() throws IOException {
        // Arrange
        createTestFiles("old_image.jpg", "new_image.jpg");
        // Explicitly set newer modified time for new_image.jpg
        Files.setLastModifiedTime(
                Paths.get(testUploadDir, "new_image.jpg"),
                FileTime.from(Instant.now())
        );

        // Act
        List<ImageDisplayDTO> result = imageService.getRecentImagesForDisplay();

        // Assert
        assertEquals(2, result.size());
        assertEquals("new_image.jpg", result.get(0).getFilename());
    }

    @Test
    void getRecentImagesForDisplay_shouldCleanDisplayNames() throws IOException {
        // Arrange
        createTestFiles("product_123456789.jpg");

        // Act
        List<ImageDisplayDTO> result = imageService.getRecentImagesForDisplay();

        // Assert
        assertEquals("product", result.get(0).getDisplayName());
    }

    @Test
    void getRecentImagesForDisplay_shouldIncludeFullUrl() throws IOException {
        // Arrange
        createTestFiles(testImageName);

        // Act
        List<ImageDisplayDTO> result = imageService.getRecentImagesForDisplay();

        // Assert
        assertTrue(result.get(0).getUrl().contains("http://localhost:8087/api/products/images/"));
        assertTrue(result.get(0).getUrl().endsWith(testImageName));
    }
}
