package com.home.growme.produt.service.model.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ImageDisplayDTO Tests")
class ImageDisplayDTOTests {
    private static final String VALID_FILENAME = "prod_12345.jpg";
    private static final String VALID_DISPLAY_NAME = "Product Preview";
    private static final String VALID_URL = "https://cdn.growme.com/images/prod_12345.jpg";

    private ImageDisplayDTO defaultDto;

    @BeforeEach
    void setUp() {
        defaultDto = createImageDisplayDTO(VALID_FILENAME, VALID_DISPLAY_NAME, VALID_URL);
    }

    private ImageDisplayDTO createImageDisplayDTO(String filename, String displayName, String url) {
        return new ImageDisplayDTO(filename, displayName, url);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        @Test
        @DisplayName("Should create valid ImageDisplayDTO with all fields")
        void shouldCreateValidImageDisplayDTOWithAllFields() {
            assertEquals(VALID_FILENAME, defaultDto.getFilename());
            assertEquals(VALID_DISPLAY_NAME, defaultDto.getDisplayName());
            assertEquals(VALID_URL, defaultDto.getUrl());
        }

        @Test
        @DisplayName("Should handle null values")
        void shouldHandleNullValues() {
            ImageDisplayDTO nullFileNameDto = createImageDisplayDTO(null, VALID_DISPLAY_NAME, VALID_URL);
            assertNull(nullFileNameDto.getFilename());

            ImageDisplayDTO nullDisplayNameDto = createImageDisplayDTO(VALID_FILENAME, null, VALID_URL);
            assertNull(nullDisplayNameDto.getDisplayName());

            ImageDisplayDTO nullUrlDto = createImageDisplayDTO(VALID_FILENAME, VALID_DISPLAY_NAME, null);
            assertNull(nullUrlDto.getUrl());
        }

        @Test
        @DisplayName("Should handle empty values")
        void shouldHandleEmptyValues() {
            ImageDisplayDTO emptyDto = createImageDisplayDTO("", "", "");
            assertEquals("", emptyDto.getFilename());
            assertEquals("", emptyDto.getDisplayName());
            assertEquals("", emptyDto.getUrl());
        }
    }

    @Nested
    @DisplayName("Object Methods Tests")
    class ObjectMethodsTests {
        private static final String SAMPLE_FILENAME = "prod_123.jpg";
        private static final String SAMPLE_DISPLAY_NAME = "Preview";
        private static final String SAMPLE_URL = "https://example.com/image.jpg";

        @Test
        @DisplayName("Should have correct toString implementation")
        void shouldHaveCorrectToStringImplementation() {
            ImageDisplayDTO dto = createImageDisplayDTO(SAMPLE_FILENAME, SAMPLE_DISPLAY_NAME, SAMPLE_URL);
            String expectedString = String.format("ImageDisplayDTO(filename=%s, displayName=%s, url=%s)",
                    SAMPLE_FILENAME, SAMPLE_DISPLAY_NAME, SAMPLE_URL);
            assertEquals(expectedString, dto.toString());
        }

        @Test
        @DisplayName("Should implement proper equals and hashCode")
        void shouldImplementProperEqualsAndHashCode() {
            ImageDisplayDTO dto1 = createImageDisplayDTO(SAMPLE_FILENAME, SAMPLE_DISPLAY_NAME, SAMPLE_URL);
            ImageDisplayDTO dto2 = createImageDisplayDTO(SAMPLE_FILENAME, SAMPLE_DISPLAY_NAME, SAMPLE_URL);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal with different fields")
        void shouldNotBeEqualWithDifferentFields() {
            ImageDisplayDTO dto1 = createImageDisplayDTO(SAMPLE_FILENAME, SAMPLE_DISPLAY_NAME, SAMPLE_URL);
            ImageDisplayDTO dto2 = createImageDisplayDTO("different.jpg", SAMPLE_DISPLAY_NAME, SAMPLE_URL);

            assertNotEquals(dto1, dto2);
        }
    }
}