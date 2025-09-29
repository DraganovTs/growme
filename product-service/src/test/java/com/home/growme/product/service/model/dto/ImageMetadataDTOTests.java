package com.home.growme.product.service.model.dto;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ImageMetadataDTO Tests")
public class ImageMetadataDTOTests {
    private static final String VALID_URL = "https://storage.growme.com/images/prod_12345.jpg";
    private static final String VALID_FILENAME = "prod_12345.jpg";
    private static final long VALID_SIZE = 245678L;
    private static final String VALID_CONTENT_TYPE = "image/jpeg";

    private ImageMetadataDTO createValidImageMetadataDTO() {
        return new ImageMetadataDTO(
                VALID_URL,
                VALID_FILENAME,
                VALID_SIZE,
                VALID_CONTENT_TYPE
        );
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        @Test
        @DisplayName("Should create valid DTO with all fields")
        void shouldCreateValidDTO() {
            ImageMetadataDTO dto = createValidImageMetadataDTO();

            assertAll(
                    () -> assertEquals(VALID_URL, dto.getUrl()),
                    () -> assertEquals(VALID_FILENAME, dto.getFilename()),
                    () -> assertEquals(VALID_SIZE, dto.getSize()),
                    () -> assertEquals(VALID_CONTENT_TYPE, dto.getContentType())
            );
        }

        @Test
        @DisplayName("Should handle null fields")
        void shouldHandleNullFields() {
            ImageMetadataDTO dto = new ImageMetadataDTO(null, null, 0L, null);

            assertAll(
                    () -> assertNull(dto.getUrl()),
                    () -> assertNull(dto.getFilename()),
                    () -> assertEquals(0L, dto.getSize()),
                    () -> assertNull(dto.getContentType())
            );
        }

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            ImageMetadataDTO dto = new ImageMetadataDTO("", "", 0L, "");

            assertAll(
                    () -> assertEquals("", dto.getUrl()),
                    () -> assertEquals("", dto.getFilename()),
                    () -> assertEquals("", dto.getContentType())
            );
        }
    }

    @Nested
    @DisplayName("Field Validation Tests")
    class FieldValidationTests {
        @ParameterizedTest
        @ValueSource(longs = {0L, 1L, 1024L, Long.MAX_VALUE})
        @DisplayName("Should handle valid file sizes")
        void shouldHandleValidFileSizes(long size) {
            ImageMetadataDTO dto = new ImageMetadataDTO(VALID_URL, VALID_FILENAME, size, VALID_CONTENT_TYPE);
            assertEquals(size, dto.getSize());
        }

        @ParameterizedTest
        @ValueSource(strings = {"image/jpeg", "image/png", "image/gif", "image/webp"})
        @DisplayName("Should handle valid content types")
        void shouldHandleValidContentTypes(String contentType) {
            ImageMetadataDTO dto = new ImageMetadataDTO(VALID_URL, VALID_FILENAME, VALID_SIZE, contentType);
            assertEquals(contentType, dto.getContentType());
        }
    }

    @Nested
    @DisplayName("Object Contract Tests")
    class ObjectContractTests {
        @Test
        @DisplayName("Should implement proper equals and hashCode")
        void shouldImplementProperEqualsAndHashCode() {
            ImageMetadataDTO dto1 = createValidImageMetadataDTO();
            ImageMetadataDTO dto2 = createValidImageMetadataDTO();

            assertAll(
                    () -> assertEquals(dto1, dto2),
                    () -> assertEquals(dto1.hashCode(), dto2.hashCode())
            );
        }

        @Test
        @DisplayName("Should have correct toString representation")
        void shouldHaveCorrectToStringRepresentation() {
            ImageMetadataDTO dto = createValidImageMetadataDTO();
            String expected = String.format("ImageMetadataDTO(url=%s, filename=%s, size=%d, contentType=%s)",
                    VALID_URL, VALID_FILENAME, VALID_SIZE, VALID_CONTENT_TYPE);
            assertEquals(expected, dto.toString());
        }

        @Test
        @DisplayName("Should not be equal with different values")
        void shouldNotBeEqualWithDifferentValues() {
            ImageMetadataDTO dto1 = createValidImageMetadataDTO();
            ImageMetadataDTO dto2 = new ImageMetadataDTO(
                    "https://example.com/image2.jpg",
                    "image2.jpg",
                    2048L,
                    "image/png"
            );
            assertNotEquals(dto1, dto2);
        }
    }
}