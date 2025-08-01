package com.home.growme.produt.service.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductResponseDTO Tests")
class ProductResponseDTOTests {
    private static final UUID TEST_PRODUCT_ID = UUID.randomUUID();
    private static final UUID TEST_OWNER_ID = UUID.randomUUID();
    private static final UUID TEST_CATEGORY_ID = UUID.randomUUID();
    private static final String TEST_PRODUCT_NAME = "Organic Apple";
    private static final String TEST_BRAND = "NatureFresh";
    private static final String TEST_DESCRIPTION = "Fresh organic apples grown without pesticides";
    private static final BigDecimal TEST_PRICE = new BigDecimal("3.49");
    private static final String TEST_IMAGE_URL = "https://example.com/image.jpg";
    private static final String TEST_CATEGORY_NAME = "Fruits";
    private static final String TEST_OWNER_NAME = "John Doe";

    private ProductResponseDTO createTestDto(boolean includeAllFields) {
        ProductResponseDTO.ProductResponseDTOBuilder builder = ProductResponseDTO.builder();
        if (includeAllFields) {
            return builder
                    .productId(TEST_PRODUCT_ID)
                    .name(TEST_PRODUCT_NAME)
                    .brand(TEST_BRAND)
                    .description(TEST_DESCRIPTION)
                    .price(TEST_PRICE)
                    .unitsInStock(100)
                    .imageUrl(TEST_IMAGE_URL)
                    .categoryName(TEST_CATEGORY_NAME)
                    .ownerName(TEST_OWNER_NAME)
                    .ownerId(TEST_OWNER_ID)
                    .productCategoryId(TEST_CATEGORY_ID)
                    .build();
        }
        return builder.build();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        @Test
        @DisplayName("Should create DTO with all fields populated")
        void shouldCreateWithAllFields() {
            ProductResponseDTO dto = createTestDto(true);

            assertEquals(TEST_PRODUCT_ID, dto.getProductId());
            assertEquals(TEST_PRODUCT_NAME, dto.getName());
            assertEquals(TEST_BRAND, dto.getBrand());
            assertEquals(TEST_DESCRIPTION, dto.getDescription());
            assertEquals(TEST_PRICE, dto.getPrice());
            assertEquals(100, dto.getUnitsInStock());
            assertEquals(TEST_IMAGE_URL, dto.getImageUrl());
            assertEquals(TEST_CATEGORY_NAME, dto.getCategoryName());
            assertEquals(TEST_OWNER_NAME, dto.getOwnerName());
            assertEquals(TEST_OWNER_ID, dto.getOwnerId());
            assertEquals(TEST_CATEGORY_ID, dto.getProductCategoryId());
        }

        @Test
        @DisplayName("Should create DTO with null fields")
        void shouldCreateWithNullFields() {
            ProductResponseDTO dto = createTestDto(false);

            assertNull(dto.getProductId());
            assertNull(dto.getName());
            assertNull(dto.getBrand());
            assertNull(dto.getDescription());
            assertNull(dto.getPrice());
            assertNull(dto.getImageUrl());
            assertNull(dto.getCategoryName());
            assertNull(dto.getOwnerName());
            assertNull(dto.getOwnerId());
            assertNull(dto.getProductCategoryId());
            assertEquals(0, dto.getUnitsInStock());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {
        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            ProductResponseDTO dto = ProductResponseDTO.builder()
                    .name("")
                    .brand("")
                    .description("")
                    .imageUrl("")
                    .categoryName("")
                    .ownerName("")
                    .build();

            assertAll("Empty Strings",
                    () -> assertEquals("", dto.getName()),
                    () -> assertEquals("", dto.getBrand()),
                    () -> assertEquals("", dto.getDescription()),
                    () -> assertEquals("", dto.getImageUrl()),
                    () -> assertEquals("", dto.getCategoryName()),
                    () -> assertEquals("", dto.getOwnerName())
            );
        }

        @Test
        @DisplayName("Should handle minimum values")
        void shouldHandleMinimumValues() {
            ProductResponseDTO dto = ProductResponseDTO.builder()
                    .price(new BigDecimal("0.01"))
                    .unitsInStock(0)
                    .build();

            assertEquals(new BigDecimal("0.01"), dto.getPrice());
            assertEquals(0, dto.getUnitsInStock());
        }
    }

    @Nested
    @DisplayName("Object Method Tests")
    class ObjectMethodTests {
        @Test
        @DisplayName("Should implement equals and hashCode correctly")
        void shouldImplementEqualsAndHashCode() {
            ProductResponseDTO dto1 = createTestDto(true);
            ProductResponseDTO dto2 = createTestDto(true);
            ProductResponseDTO differentDto = ProductResponseDTO.builder()
                    .productId(UUID.randomUUID())
                    .build();

            assertAll("Equals and HashCode",
                    () -> assertEquals(dto1, dto2),
                    () -> assertEquals(dto1.hashCode(), dto2.hashCode()),
                    () -> assertNotEquals(dto1, differentDto)
            );
        }

        @Test
        @DisplayName("Should implement toString correctly")
        void shouldImplementToString() {
            ProductResponseDTO dto = createTestDto(true);
            String toString = dto.toString();

            assertTrue(toString.contains(TEST_PRODUCT_ID.toString()));
            assertTrue(toString.contains(TEST_PRODUCT_NAME));
            assertTrue(toString.contains(TEST_PRICE.toString()));
        }
    }
}