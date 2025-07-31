package com.home.order.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.order.service.exception.BasketNotFoundException;
import com.home.order.service.model.dto.BasketData;
import com.home.order.service.model.entity.BasketItem;
import com.home.order.service.service.BasketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BasketController.class)
@ActiveProfiles("test")
public class BasketControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BasketService basketService;

    @Autowired
    private ObjectMapper objectMapper;

    private BasketData sampleBasket;

    @BeforeEach
    void setUp() {
        sampleBasket = BasketData.builder()
                .id("basket123")
                .items(Collections.singletonList(
                        BasketItem.builder()
                                .productId(UUID.randomUUID())
                                .quantity(1)
                                .name("test product")
                                .unitsInStock(2)
                                .imageUrl("image-1")
                                .price(BigDecimal.valueOf(10.99))
                                .brandName("pink")
                                .categoryName("fruits")
                                .build()
                ))
                .deliveryMethodId(1)
                .clientSecret("secret-client")
                .paymentIntentId("intentId")
                .build();
    }

    @Test
    void createOrUpdateBasket_ShouldReturnSavedBasket() throws Exception {
        when(basketService.createOrUpdateBasket(any(BasketData.class))).thenReturn(sampleBasket);

        mockMvc.perform(post("/api/basket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBasket)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("basket123"))
                .andExpect(jsonPath("$.paymentIntentId").value("intentId"))
                .andExpect(jsonPath("$.clientSecret").value("secret-client"));

        verify(basketService).createOrUpdateBasket(any(BasketData.class));
    }

    @Test
    void createOrUpdateBasket_ShouldReturn400_WhenInvalidInput() throws Exception {
        BasketData invalidBasket = BasketData.builder().build(); // Missing required fields

        mockMvc.perform(post("/api/basket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBasket)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));

        verify(basketService, never()).createOrUpdateBasket(any());
    }

    @Test
    void getBasketById_ShouldReturnBasket() throws Exception {
        when(basketService.getBasketById("basket123")).thenReturn(sampleBasket);

        mockMvc.perform(get("/api/basket/basket123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("basket123"))
                .andExpect(jsonPath("$.paymentIntentId").value("intentId"));
    }

    @Test
    void getBasketById_ShouldReturn404_WhenBasketNotFound() throws Exception {
        when(basketService.getBasketById("nonexistent"))
                .thenThrow(new BasketNotFoundException("Basket not found"));

        mockMvc.perform(get("/api/basket/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Basket not found"));

        verify(basketService).getBasketById("nonexistent");
    }

    @Test
    void deleteBasket_ShouldReturn204() throws Exception {
        doNothing().when(basketService).deleteBasket("basket123");

        mockMvc.perform(delete("/api/basket/basket123"))
                .andExpect(status().isNoContent());

        verify(basketService).deleteBasket("basket123");
    }

    @Test
    void deleteBasket_ShouldReturn404_WhenBasketNotFound() throws Exception {
        doThrow(new BasketNotFoundException("Basket not found"))
                .when(basketService).deleteBasket("nonexistent");

        mockMvc.perform(delete("/api/basket/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Basket not found"));

        verify(basketService).deleteBasket("nonexistent");
    }
}