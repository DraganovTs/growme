package com.home.growme.product.service.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.growme.product.service.model.dto.ImageDisplayDTO;
import com.home.growme.product.service.model.dto.ProductRequestDTO;
import com.home.growme.product.service.model.dto.ProductResponseDTO;
import com.home.growme.product.service.model.dto.ProductResponseListDTO;
import com.home.growme.product.service.service.ImageService;
import com.home.growme.product.service.service.ProductService;
import com.home.growme.product.service.specification.ProductSpecParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
public class ProductControllerTests {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ImageService imageService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductResponseDTO testProduct;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        testProduct = ProductResponseDTO.builder()
                .productId(UUID.randomUUID())
                .name("Test Product")
                .price(BigDecimal.valueOf(9.99))
                .build();
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return product by ID")
    void getProductById_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(testProduct.getProductId().toString())).thenReturn(testProduct);

        mockMvc.perform(get("/api/products/" + testProduct.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(testProduct.getProductId().toString()))
                .andExpect(jsonPath("$.name").value(testProduct.getName()));

        verify(productService).getProductById(testProduct.getProductId().toString());
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should handle not found")
    void getProductById_ShouldHandleNotFound() throws Exception {
        when(productService.getProductById(anyString())).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/products/some-id"))
                .andExpect(status().isInternalServerError());

        verify(productService).getProductById("some-id");
    }

    @Test
    @DisplayName("GET /api/products - Should return all products")
    void getAllProducts_ShouldReturnProducts() throws Exception {
        ProductResponseListDTO responseList =  ProductResponseListDTO.builder()
                .dataList(Collections.singletonList(testProduct))
                .build();


        when(productService.getAllProducts(any(ProductSpecParams.class))).thenReturn(responseList);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataList[0].productId").value(testProduct.getProductId().toString()));

        verify(productService).getAllProducts(any());
    }

    @Test
    @DisplayName("POST /api/products - Should create new product")
    void createProduct_ShouldSucceed() throws Exception {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(9.99))
                .categoryName("Fruits")
                .ownerId(UUID.randomUUID())
                .build();

        ProductResponseDTO response = ProductResponseDTO.builder()
                .productId(UUID.randomUUID())
                .name("Test Product")
                .price(BigDecimal.valueOf(9.99))
                .build();

        when(productService.createProduct(any())).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService).createProduct(any());
    }



    @Test
    @DisplayName("POST /api/products - Should handle validation error")
    void createProduct_ShouldHandleValidationError() throws Exception {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("") // invalid name
                .price(BigDecimal.valueOf(9.99))
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products - Should handle service exception gracefully")
    void createProduct_ShouldHandleServiceException() throws Exception {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(9.99))
                .categoryName("Fruits")
                .ownerId(UUID.randomUUID())
                .build();

        when(productService.createProduct(any())).thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }


    @Test
    @DisplayName("GET /api/products/images/recent - Should return recent images")
    void getRecentImages_ShouldReturnList() throws Exception {
        List<ImageDisplayDTO> images = Collections.singletonList(new ImageDisplayDTO("img.jpg", "name","/images/img.jpg"));

        when(imageService.getRecentImagesForDisplay()).thenReturn(images);

        mockMvc.perform(get("/api/products/images/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filename").value("img.jpg"));
    }

    @Test
    @DisplayName("GET /api/products/images/recent - Should handle service exception")
    void getRecentImages_ShouldHandleServiceException() throws Exception {
        when(imageService.getRecentImagesForDisplay()).thenThrow(new RuntimeException("Internal failure"));

        mockMvc.perform(get("/api/products/images/recent"))
                .andExpect(status().isInternalServerError());
    }
}
