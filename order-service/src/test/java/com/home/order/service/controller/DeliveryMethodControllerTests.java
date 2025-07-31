package com.home.order.service.controller;
import com.home.order.service.model.dto.DeliveryMethodDTO;
import com.home.order.service.service.DeliveryMethodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryMethodController.class)
@ActiveProfiles("test")
public class DeliveryMethodControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryMethodService deliveryMethodService;

    @Test
    public void getAllDeliveryMethods_ShouldReturnListOfDeliveryMethods() throws Exception {
        // Arrange
        DeliveryMethodDTO method1 = DeliveryMethodDTO.builder()
                .deliveryMethodId(1)
                .shortName("Standard Shipping")
                .deliveryTime("1-2 business days")
                .description("Fast delivery with tracking")
                .price(new BigDecimal("5.99"))
                .build();

        DeliveryMethodDTO method2 = DeliveryMethodDTO.builder()
                .deliveryMethodId(2)
                .shortName("Express Shipping")
                .deliveryTime("1-2 business days")
                .description("Fast delivery without tracking")
                .price(new BigDecimal("12.99"))
                .build();

        List<DeliveryMethodDTO> methods = Arrays.asList(method1, method2);

        when(deliveryMethodService.getAllDeliveryMethods()).thenReturn(methods);

        // Act & Assert
        mockMvc.perform(get("/api/deliverymethods")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deliveryMethodId").value(1))
                .andExpect(jsonPath("$[0].shortName").value("Standard Shipping"))
                .andExpect(jsonPath("$[0].price").value(5.99))
                .andExpect(jsonPath("$[1].deliveryMethodId").value(2))
                .andExpect(jsonPath("$[1].shortName").value("Express Shipping"))
                .andExpect(jsonPath("$[1].price").value(12.99));

        verify(deliveryMethodService).getAllDeliveryMethods();
    }

    @Test
    public void getAllDeliveryMethods_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(deliveryMethodService.getAllDeliveryMethods()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/deliverymethods")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(deliveryMethodService).getAllDeliveryMethods();
    }

    @Test
    public void getAllDeliveryMethods_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(deliveryMethodService.getAllDeliveryMethods())
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        mockMvc.perform(get("/api/deliverymethods")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(deliveryMethodService).getAllDeliveryMethods();
    }
}

