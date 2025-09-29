package com.home.growme.product.service.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductResponseListDTO Tests")
class ProductResponseListDTOTests {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_TOTAL_PAGES = 3;
    private static final long DEFAULT_TOTAL_COUNT = 25L;
    private static final int DEFAULT_PAGE_INDEX = 1;
    private static final String PRODUCT_NAME_1 = "Product 1";
    private static final String PRODUCT_NAME_2 = "Product 2";
    private static final BigDecimal PRICE_1 = new BigDecimal("19.99");
    private static final BigDecimal PRICE_2 = new BigDecimal("29.99");

    private final TestProductFactory productFactory = new TestProductFactory();

    @Nested
    @DisplayName("Basic DTO Operations")
    class BasicOperations {
        @Test
        @DisplayName("Should create valid DTO with all fields populated")
        void shouldCreateValidDTO() {
            List<ProductResponseDTO> products = Arrays.asList(
                    productFactory.createProduct(PRODUCT_NAME_1, PRICE_1),
                    productFactory.createProduct(PRODUCT_NAME_2, PRICE_2)
            );

            ProductResponseListDTO dto = createDefaultDTO(products);

            assertDTOFields(dto, DEFAULT_TOTAL_PAGES, DEFAULT_TOTAL_COUNT,
                    DEFAULT_PAGE_INDEX, DEFAULT_PAGE_SIZE);
            assertProductList(dto, products);
        }

        @Test
        @DisplayName("Should handle empty product list")
        void shouldHandleEmptyList() {
            ProductResponseListDTO dto = ProductResponseListDTO.builder()
                    .totalPages(0)
                    .totalCount(0L)
                    .pageIndex(0)
                    .pageSize(DEFAULT_PAGE_SIZE)
                    .dataList(Collections.emptyList())
                    .build();

            assertDTOFields(dto, 0, 0L, 0, DEFAULT_PAGE_SIZE);
            assertTrue(dto.getDataList().isEmpty());
        }

        @Test
        @DisplayName("Should handle null product list")
        void shouldHandleNullList() {
            ProductResponseListDTO dto = createDefaultDTO(null);
            assertNull(dto.getDataList());
        }
    }

    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 10, Integer.MAX_VALUE})
        @DisplayName("Should handle various page sizes")
        void shouldHandleValidPageSizes(int pageSize) {
            ProductResponseListDTO dto = ProductResponseListDTO.builder()
                    .pageSize(pageSize)
                    .build();
            assertEquals(pageSize, dto.getPageSize());
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, 1L, 100L, Long.MAX_VALUE})
        @DisplayName("Should handle various total counts")
        void shouldHandleValidTotalCounts(long totalCount) {
            ProductResponseListDTO dto = ProductResponseListDTO.builder()
                    .totalCount(totalCount)
                    .build();
            assertEquals(totalCount, dto.getTotalCount());
        }
    }

    @Nested
    @DisplayName("Object Contract Tests")
    class ObjectContractTests {
        @Test
        @DisplayName("Should implement equals and hashCode correctly")
        void shouldImplementEqualsAndHashCode() {
            List<ProductResponseDTO> products = Collections.singletonList(
                    productFactory.createProduct("Test Product", new BigDecimal("9.99"))
            );

            ProductResponseListDTO dto1 = createDefaultDTO(products);
            ProductResponseListDTO dto2 = createDefaultDTO(products);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should have proper string representation")
        void shouldHaveProperToString() {
            ProductResponseDTO product = productFactory.createProduct("Sample", new BigDecimal("5.99"));
            ProductResponseListDTO dto = createDefaultDTO(Collections.singletonList(product));

            String toString = dto.toString();
            assertTrue(toString.contains("totalPages=" + DEFAULT_TOTAL_PAGES));
            assertTrue(toString.contains("totalCount=" + DEFAULT_TOTAL_COUNT));
            assertTrue(toString.contains("Sample"));
        }
    }

    private static class TestProductFactory {
        ProductResponseDTO createProduct(String name, BigDecimal price) {
            return ProductResponseDTO.builder()
                    .productId(UUID.randomUUID())
                    .name(name)
                    .price(price)
                    .build();
        }
    }

    private ProductResponseListDTO createDefaultDTO(List<ProductResponseDTO> products) {
        return ProductResponseListDTO.builder()
                .totalPages(DEFAULT_TOTAL_PAGES)
                .totalCount(DEFAULT_TOTAL_COUNT)
                .pageIndex(DEFAULT_PAGE_INDEX)
                .pageSize(DEFAULT_PAGE_SIZE)
                .dataList(products)
                .build();
    }

    private void assertDTOFields(ProductResponseListDTO dto, int totalPages,
                                 long totalCount, int pageIndex, int pageSize) {
        assertAll(
                () -> assertEquals(totalPages, dto.getTotalPages()),
                () -> assertEquals(totalCount, dto.getTotalCount()),
                () -> assertEquals(pageIndex, dto.getPageIndex()),
                () -> assertEquals(pageSize, dto.getPageSize())
        );
    }

    private void assertProductList(ProductResponseListDTO dto, List<ProductResponseDTO> expectedProducts) {
        assertEquals(expectedProducts.size(), dto.getDataList().size());
        for (int i = 0; i < expectedProducts.size(); i++) {
            ProductResponseDTO expected = expectedProducts.get(i);
            ProductResponseDTO actual = dto.getDataList().get(i);
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getPrice(), actual.getPrice());
        }
    }
}