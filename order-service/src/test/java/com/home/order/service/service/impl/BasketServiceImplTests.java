package com.home.order.service.service.impl;

import com.home.order.service.exception.BasketNotFoundException;
import com.home.order.service.mapper.BasketMapper;
import com.home.order.service.model.dto.BasketData;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.model.entity.BasketItem;
import com.home.order.service.repository.BasketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasketServiceImplTests {
    @Mock
    private BasketRepository basketRepository;

    @Mock
    private BasketMapper basketMapper;

    @InjectMocks
    private BasketServiceImpl basketService;

    private final String basketId = "test-basket-id";
    private Basket testBasket;
    private BasketData testBasketData;

    @BeforeEach
    void setUp() {
        BasketItem item = BasketItem.builder()
                .productId(UUID.randomUUID())
                .quantity(2)
                .price(BigDecimal.valueOf(9.99))
                .build();

        testBasket = Basket.builder()
                .id(basketId)
                .items(List.of(item))
                .deliveryMethodId(1)
                .shippingPrice(BigDecimal.valueOf(5.00))
                .clientSecret("secret")
                .paymentIntentId("intent-123")
                .build();

        testBasketData = new BasketData();
        testBasketData.setId(basketId);
        testBasketData.setItems(List.of(item));
        testBasketData.setDeliveryMethodId(1);
        testBasketData.setShippingPrice(BigDecimal.valueOf(5.00));
    }

    @Test
    @DisplayName("Should create new basket when not present")
    void shouldCreateNewBasketWhenNotPresent() {
        when(basketRepository.findById(basketId)).thenReturn(Optional.empty());
        when(basketMapper.mapBasketDataToBasket(any(Basket.class), eq(testBasketData))).thenReturn(testBasket);
        when(basketRepository.save(any(Basket.class))).thenReturn(testBasket);
        when(basketMapper.mapBasketToBasketData(testBasket)).thenReturn(testBasketData);

        BasketData result = basketService.createOrUpdateBasket(testBasketData);

        assertEquals(testBasketData.getId(), result.getId());
        verify(basketRepository, times(2)).save(testBasket); // saved twice (in original code)
    }

    @Test
    @DisplayName("Should update existing basket when present")
    void shouldUpdateExistingBasketWhenPresent() {
        when(basketRepository.findById(basketId)).thenReturn(Optional.of(testBasket));
        when(basketMapper.mapBasketDataToBasket(eq(testBasket), eq(testBasketData))).thenReturn(testBasket);
        when(basketRepository.save(testBasket)).thenReturn(testBasket);
        when(basketMapper.mapBasketToBasketData(testBasket)).thenReturn(testBasketData);

        BasketData result = basketService.createOrUpdateBasket(testBasketData);

        assertEquals(testBasketData.getId(), result.getId());
        verify(basketRepository, times(2)).save(testBasket); // again, called twice
    }

    @Test
    @DisplayName("Should get basket by id successfully")
    void shouldGetBasketByIdSuccessfully() {
        when(basketRepository.findById(basketId)).thenReturn(Optional.of(testBasket));
        when(basketMapper.mapBasketToBasketData(testBasket)).thenReturn(testBasketData);

        BasketData result = basketService.getBasketById(basketId);

        assertEquals(testBasketData.getId(), result.getId());
        verify(basketRepository).findById(basketId);
    }

    @Test
    @DisplayName("Should throw BasketNotFoundException when basket not found")
    void shouldThrowWhenBasketNotFound() {
        when(basketRepository.findById(basketId)).thenReturn(Optional.empty());

        BasketNotFoundException ex = assertThrows(BasketNotFoundException.class,
                () -> basketService.getBasketById(basketId));

        assertTrue(ex.getMessage().contains(basketId));
    }

    @Test
    @DisplayName("Should delete basket by id")
    void shouldDeleteBasketById() {
        basketService.deleteBasket(basketId);

        verify(basketRepository).deleteById(basketId);
    }

}
