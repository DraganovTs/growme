package com.home.growme.product.service.service.impl;

import com.home.growme.common.module.dto.*;
import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.exceptions.CategoryNotFoundException;
import com.home.growme.product.service.exception.OwnerNotFoundException;
import com.home.growme.product.service.exception.ProductNotFoundException;
import com.home.growme.product.service.exception.StockInsufficientException;
import com.home.growme.product.service.model.dto.ProductRequestDTO;
import com.home.growme.product.service.model.dto.ProductResponseDTO;
import com.home.growme.product.service.model.dto.ProductResponseListDTO;
import com.home.growme.product.service.model.entity.Category;
import com.home.growme.product.service.model.entity.Owner;
import com.home.growme.product.service.model.entity.Product;
import com.home.growme.product.service.repository.CategoryRepository;
import com.home.growme.product.service.repository.OwnerRepository;
import com.home.growme.product.service.repository.ProductRepository;
import com.home.growme.product.service.mapper.ProductMapper;
import com.home.growme.product.service.service.EventPublisherService;
import com.home.growme.product.service.specification.ProductSpecParams;
import com.home.growme.product.service.specification.ProductSpecificationNameOwnerCategory;
import com.home.growme.product.service.util.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductSpecificationNameOwnerCategory productSpecification;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private EventPublisherService eventPublisherService;

    @Mock
    private ProductValidator productValidator;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;
    private Category category;
    private Owner owner;
    private UUID productId;
    private UUID ownerId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName("Electronics");

        owner = new Owner();
        owner.setOwnerId(ownerId);
        owner.setOwnerName("John Doe");

        product = new Product();
        product.setProductId(productId);
        product.setName("Smartphone");
        product.setBrand("Samsung");
        product.setPrice(BigDecimal.valueOf(999.99));
        product.setUnitsInStock(10);
        product.setCategory(category);
        product.setOwner(owner);

        productRequestDTO = ProductRequestDTO.builder()
                .name("Smartphone")
                .brand("Samsung")
                .price(BigDecimal.valueOf(999.99))
                .unitsInStock(10)
                .categoryName("Electronics")
                .ownerId(ownerId)
                .build();

        productResponseDTO = ProductResponseDTO.builder()
                .productId(productId)
                .name("Smartphone")
                .brand("Samsung")
                .price(BigDecimal.valueOf(999.99))
                .unitsInStock(10)
                .categoryName("Electronics")
                .ownerName("John Doe")
                .build();
    }

    @Test
    void getProductById_shouldReturnProduct_whenExists() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.mapProductToProductResponseDTO(product)).thenReturn(productResponseDTO);

        // Act
        ProductResponseDTO result = productService.getProductById(productId.toString());

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        verify(productRepository).findById(productId);
    }

    @Test
    void getProductById_shouldThrowException_whenNotFound() {
        // Arrange
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class,
                () -> productService.getProductById(productId.toString()));
    }

    @Test
    void createProduct_shouldCreateAndReturnProduct_whenValidRequest() {
        // Arrange
        when(categoryRepository.findByCategoryName("Electronics")).thenReturn(Optional.of(category));
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(productMapper.mapProductRequestDTOToProduct(productRequestDTO, category, owner))
                .thenReturn(product);
        when(productMapper.mapProductToProductResponseDTO(product)).thenReturn(productResponseDTO);

        // Act
        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        // Assert
        assertNotNull(result);
        verify(productRepository).save(product);
        verify(eventPublisherService).publishProductAssignment(ownerId.toString(), productId.toString());
    }

    @Test
    void createProduct_shouldThrowException_whenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findByCategoryName("Electronics")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class,
                () -> productService.createProduct(productRequestDTO));
    }

    @Test
    void createProduct_shouldThrowException_whenOwnerNotFound() {
        // Arrange
        when(categoryRepository.findByCategoryName("Electronics")).thenReturn(Optional.of(category));
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OwnerNotFoundException.class,
                () -> productService.createProduct(productRequestDTO));
    }

    @Test
    void updateProduct_shouldUpdateAndReturnProduct_whenExists() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.mapProductToProductResponseDTO(product)).thenReturn(productResponseDTO);

        // Act
        ProductResponseDTO result = productService.updateProduct(productId.toString(), productRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Smartphone", result.getName());
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_shouldThrowException_whenNotFound() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProduct(productId.toString(), productRequestDTO));
    }

    @Test
    void deleteProduct_shouldDeleteProduct_whenExists() {
        // Act
        productService.deleteProduct(productId.toString(), ownerId.toString());

        // Assert
        verify(productRepository).deleteById(productId);
        verify(eventPublisherService).publishProductDeletion(productId.toString(), ownerId.toString());
    }

    @Test
    void deleteProduct_shouldNotThrow_whenProductNotExists() {
        // Act & Assert
        assertDoesNotThrow(() ->
                productService.deleteProduct(productId.toString(), ownerId.toString()));

        // Verify deletion was attempted
        verify(productRepository).deleteById(productId);
    }

    @Test
    void validateProducts_shouldReturnValidResult_whenAllConditionsMet() {
        // Arrange
        BasketItemDTO item = new BasketItemDTO(productId, 2, BigDecimal.valueOf(999.99));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        List<ProductValidationResult> results = productService.validateProducts(List.of(item));

        // Assert
        assertEquals(1, results.size());
        assertTrue(results.get(0).getValid());
    }

    @Test
    void validateProducts_shouldReturnInvalidResult_whenStockInsufficient() {
        // Arrange
        BasketItemDTO item = new BasketItemDTO(productId, 20, BigDecimal.valueOf(999.99));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        List<ProductValidationResult> results = productService.validateProducts(List.of(item));

        // Assert
        assertEquals(1, results.size());
        assertFalse(results.get(0).getValid());
        assertEquals("Insufficient stock", results.get(0).getReason());
    }

    @Test
    void completeOrder_shouldReduceStock_whenValidOrder() {
        // Arrange
        OrderItemDTO item = new OrderItemDTO(productId.toString(), 2, BigDecimal.valueOf(999.99));
        OrderCompletedEvent event = new OrderCompletedEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                "email@gmail.com", List.of(item), new BigDecimal(999.99), Instant.now());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        productService.completeOrder(event);

        // Assert
        assertEquals(8, product.getUnitsInStock());
        verify(productRepository).save(product);
    }

    @Test
    void completeOrder_shouldThrowException_whenStockInsufficient() {
        // Arrange
        OrderItemDTO item = new OrderItemDTO(productId.toString(), 20, BigDecimal.valueOf(999.99));
        OrderCompletedEvent event = new OrderCompletedEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                "email@gmail.com", List.of(item), new BigDecimal(999.99), Instant.now());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(StockInsufficientException.class,
                () -> productService.completeOrder(event));
    }



    @Test
    void getProductsByOwner_shouldFilterByOwner() {
        // Arrange
        ProductSpecParams params = new ProductSpecParams();
        params.setOwnerId(ownerId.toString());
        params.setPageSize(10);
        params.setPageIndex(1);

        // Make sure the response DTO has the owner ID set
        productResponseDTO.setOwnerId(ownerId);

        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.mapProductToProductResponseDTO(product)).thenReturn(productResponseDTO);

        // Act
        ProductResponseListDTO result = productService.getProductsByOwner(params);

        // Assert
        assertEquals(1, result.getDataList().size());
        assertEquals(ownerId, result.getDataList().get(0).getOwnerId());
    }
}


