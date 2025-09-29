package com.home.growme.product.service.controller;
import com.home.growme.product.service.model.dto.OwnerDTO;
import com.home.growme.product.service.service.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
@ActiveProfiles("test")
public class OwnerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerService ownerService;

    @InjectMocks
    private OwnerController ownerController;

    private OwnerDTO testOwnerDTO;
    private OwnerDTO testTopOwnerDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testOwnerDTO = new OwnerDTO();
        testOwnerDTO.setOwnerId(UUID.randomUUID());
        testOwnerDTO.setOwnerName("Test Owner");

        testTopOwnerDTO = new OwnerDTO();
        testTopOwnerDTO.setOwnerId(UUID.randomUUID());

    }

    @Test
    @DisplayName("GET /api/owners - Should return all owners successfully")
    void getAllOwners_ShouldReturnAllOwners() throws Exception {
        // Arrange
        List<OwnerDTO> owners = Collections.singletonList(testOwnerDTO);
        when(ownerService.getAllOwners()).thenReturn(owners);

        // Act & Assert
        mockMvc.perform(get("/api/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].ownerId").value(testOwnerDTO.getOwnerId().toString()))
                .andExpect(jsonPath("$[0].ownerName").value(testOwnerDTO.getOwnerName()));

        verify(ownerService).getAllOwners();
    }

    @Test
    @DisplayName("GET /api/owners - Should return empty list when no owners exist")
    void getAllOwners_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(ownerService.getAllOwners()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(ownerService).getAllOwners();
    }

    @Test
    @DisplayName("GET /api/owners/sorted-by-product-count - Should return owners sorted by product count")
    void getOwnersSortedByProductCount_ShouldReturnSortedOwners() throws Exception {
        // Arrange
        List<OwnerDTO> topOwners = Collections.singletonList(testTopOwnerDTO);
        when(ownerService.getAllOwnersSortedByProductCount()).thenReturn(topOwners);

        // Act & Assert
        mockMvc.perform(get("/api/owners/sorted-by-product-count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].ownerId").value(testTopOwnerDTO.getOwnerId().toString()))
                .andExpect(jsonPath("$[0].ownerName").value(testTopOwnerDTO.getOwnerName()));


        verify(ownerService).getAllOwnersSortedByProductCount();
    }

    @Test
    @DisplayName("GET /api/owners/sorted-by-product-count - Should return empty list when no owners exist")
    void getOwnersSortedByProductCount_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(ownerService.getAllOwnersSortedByProductCount()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/owners/sorted-by-product-count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(ownerService).getAllOwnersSortedByProductCount();
    }

    @Test
    @DisplayName("GET /api/owners - Should handle service exception gracefully")
    void getAllOwners_ShouldHandleServiceException() throws Exception {
        // Arrange
        when(ownerService.getAllOwners()).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(get("/api/owners"))
                .andExpect(status().isInternalServerError());

        verify(ownerService).getAllOwners();
    }

    @Test
    @DisplayName("GET /api/owners/sorted-by-product-count - Should handle service exception gracefully")
    void getOwnersSortedByProductCount_ShouldHandleServiceException() throws Exception {
        // Arrange
        when(ownerService.getAllOwnersSortedByProductCount()).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(get("/api/owners/sorted-by-product-count"))
                .andExpect(status().isInternalServerError());

        verify(ownerService).getAllOwnersSortedByProductCount();
    }
}
