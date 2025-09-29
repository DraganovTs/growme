package com.home.growme.product.service.model.dto;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ImageUploadResponse Tests")
public class ImageUploadResponseTests {

    private static final String VALID_FILENAME = "prod_67890.png";
    private static final String VALID_ORIGINAL_NAME = "product_image.png";
    private static final String VALID_URL = "/images/prod_67890.png";

    private ImageUploadResponse createResponse(String filename, String originalName, String url) {
        return new ImageUploadResponse(filename, originalName, url);
    }

    private ImageUploadResponse createValidResponse() {
        return createResponse(VALID_FILENAME, VALID_ORIGINAL_NAME, VALID_URL);
    }

    @Nested
    @DisplayName("Basic Functionality Tests")
    class BasicFunctionalityTests {
        @Test
        @DisplayName("Should create valid response with all fields")
        void shouldCreateValidResponse() {
            ImageUploadResponse response = createValidResponse();

            assertAll(
                    () -> assertEquals(VALID_FILENAME, response.getFilename()),
                    () -> assertEquals(VALID_ORIGINAL_NAME, response.getOriginalName()),
                    () -> assertEquals(VALID_URL, response.getUrl())
            );
        }

        @Test
        @DisplayName("Should handle null fields")
        void shouldHandleNullFields() {
            ImageUploadResponse response = createResponse(null, null, null);

            assertAll(
                    () -> assertNull(response.getFilename()),
                    () -> assertNull(response.getOriginalName()),
                    () -> assertNull(response.getUrl())
            );
        }

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            ImageUploadResponse response = createResponse("", "", "");

            assertAll(
                    () -> assertEquals("", response.getFilename()),
                    () -> assertEquals("", response.getOriginalName()),
                    () -> assertEquals("", response.getUrl())
            );
        }
    }

    @Nested
    @DisplayName("File Handling Tests")
    class FileHandlingTests {
        @ParameterizedTest
        @ValueSource(strings = {
                "image.png",
                "product-123.jpg",
                "2023_photo.webp",
                "long_filename_with_underscores_and_numbers_123.png"
        })
        @DisplayName("Should handle valid filenames")
        void shouldHandleValidFilenames(String filename) {
            ImageUploadResponse response = createResponse(
                    filename,
                    VALID_ORIGINAL_NAME,
                    "/images/" + filename
            );
            assertEquals(filename, response.getFilename());
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "/images/file.jpg",
                "/products/123/image.png",
                "assets/uploads/2023/photo.jpg",
                "/a/b/c/d/e/f/g/h/i/j/k/long/path/image.webp"
        })
        @DisplayName("Should handle valid URL paths")
        void shouldHandleValidUrlPaths(String urlPath) {
            ImageUploadResponse response = createResponse(
                    VALID_FILENAME,
                    VALID_ORIGINAL_NAME,
                    urlPath
            );
            assertEquals(urlPath, response.getUrl());
        }

        @Test
        @DisplayName("Should handle special characters")
        void shouldHandleSpecialCharacters() {
            String specialFilename = "prod-123_photo@2x.jpg";
            String specialOriginal = "Product Image (2023).jpg";
            String specialUrl = "/images/prod-123_photo@2x.jpg";

            ImageUploadResponse response = createResponse(
                    specialFilename,
                    specialOriginal,
                    specialUrl
            );

            assertAll(
                    () -> assertEquals(specialFilename, response.getFilename()),
                    () -> assertEquals(specialOriginal, response.getOriginalName()),
                    () -> assertEquals(specialUrl, response.getUrl())
            );
        }
    }

    @Nested
    @DisplayName("Object Contract Tests")
    class ObjectContractTests {
        @Test
        @DisplayName("Should implement equals and hashCode")
        void shouldImplementEqualsAndHashCode() {
            ImageUploadResponse response1 = createValidResponse();
            ImageUploadResponse response2 = createValidResponse();
            ImageUploadResponse different = createResponse(
                    "different.jpg",
                    "different.jpg",
                    "/images/different.jpg"
            );

            assertAll(
                    () -> assertEquals(response1, response2),
                    () -> assertEquals(response1.hashCode(), response2.hashCode()),
                    () -> assertNotEquals(response1, different)
            );
        }

        @Test
        @DisplayName("Should have correct toString")
        void shouldHaveCorrectToString() {
            ImageUploadResponse response = createValidResponse();
            String expected = String.format("ImageUploadResponse(filename=%s, originalName=%s, url=%s)",
                    VALID_FILENAME, VALID_ORIGINAL_NAME, VALID_URL);

            assertEquals(expected, response.toString());
        }
    }
}