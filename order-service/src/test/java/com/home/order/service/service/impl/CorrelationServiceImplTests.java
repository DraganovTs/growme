package com.home.order.service.service.impl;

import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.order.service.exception.BasketNotFoundException;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.repository.BasketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CorrelationServiceImplTests {

    @Mock
    private BasketRepository basketRepository;

    @InjectMocks
    private CorrelationServiceImpl correlationService;

    private final String testBasketId = "basket-123";
    private final String testCorrelationId = "corr-456";
    private final String testPaymentIntentId = "pi_123456789";

    @BeforeEach
    void setUp() {
        // Clear any state before each test
        correlationService = new CorrelationServiceImpl(basketRepository);
    }

    @Test
    void createCorrelation_ShouldGenerateAndStoreCorrelationId() {
        // Act
        String correlationId = correlationService.createCorrelation(testBasketId);

        // Assert
        assertNotNull(correlationId);
        assertFalse(correlationId.isBlank());
    }

    @Test
    void createCorrelation_WithNullBasketId_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> correlationService.createCorrelation(null));
    }

    @Test
    void createCorrelation_WithEmptyBasketId_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> correlationService.createCorrelation(""));
    }

    @Test
    void awaitResponse_ShouldCreateAndStorePendingFuture() {
        // Act
        CompletableFuture<PaymentIntentResponseEvent> future =
                correlationService.awaitResponse(testCorrelationId);

        // Assert
        assertNotNull(future);
        assertFalse(future.isDone());
    }

    @Test
    void awaitResponse_WithNullCorrelationId_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> correlationService.awaitResponse(null));
    }

    @Test
    void awaitResponse_WithEmptyCorrelationId_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> correlationService.awaitResponse(""));
    }

    @Test
    void completeResponse_ShouldCompletePendingFuture() throws ExecutionException, InterruptedException {
        // Arrange
        PaymentIntentResponseEvent response = new PaymentIntentResponseEvent(
                testCorrelationId, testPaymentIntentId, "client-secret", "succeeded");

        correlationService.createCorrelation(testBasketId);
        CompletableFuture<PaymentIntentResponseEvent> future =
                correlationService.awaitResponse(testCorrelationId);

        // Act
        correlationService.completeResponse(testCorrelationId, response);

        // Assert
        assertTrue(future.isDone());
        assertEquals(response, future.get());
    }

    @Test
    void completeResponse_ShouldStorePaymentIntentId() {
        // Arrange
        PaymentIntentResponseEvent response = new PaymentIntentResponseEvent(
                testCorrelationId, testPaymentIntentId, "client-secret", "succeeded");

        String correlationId = correlationService.createCorrelation(testBasketId);
        correlationService.awaitResponse(correlationId);

        Basket mockBasket = new Basket();
        when(basketRepository.findById(testBasketId)).thenReturn(Optional.of(mockBasket));
        when(basketRepository.save(any(Basket.class))).thenReturn(mockBasket);

        // Act
        correlationService.completeResponse(correlationId, response);

        // Assert
        verify(basketRepository).save(mockBasket);
        assertEquals(testPaymentIntentId, mockBasket.getPaymentIntentId());
    }

    @Test
    void completeResponse_WithNullCorrelationId_ShouldLogError() {
        // Arrange
        PaymentIntentResponseEvent response = new PaymentIntentResponseEvent(
                testCorrelationId, testPaymentIntentId, "client-secret", "succeeded");

        // Act
        correlationService.completeResponse(null, response);

        // Assert
        verify(basketRepository, never()).save(any());
    }

    @Test
    void completeResponse_WithUnknownCorrelationId_ShouldLogWarning() {
        // Arrange
        PaymentIntentResponseEvent response = new PaymentIntentResponseEvent(
                testCorrelationId, testPaymentIntentId, "client-secret", "succeeded");

        // Act
        correlationService.completeResponse("unknown-correlation", response);

        // Assert
        verify(basketRepository, never()).save(any());
    }

    @Test
    void getPaymentIntentId_ShouldReturnFromMemoryCache() {
        // Arrange
        String correlationId = correlationService.createCorrelation(testBasketId);
        correlationService.awaitResponse(correlationId); // Need to create pending request

        PaymentIntentResponseEvent response = new PaymentIntentResponseEvent(
                correlationId, testPaymentIntentId, "secret", "succeeded"
        );

        // Mock basket repository for the update call
        Basket mockBasket = new Basket();
        when(basketRepository.findById(testBasketId)).thenReturn(Optional.of(mockBasket));
        when(basketRepository.save(any(Basket.class))).thenReturn(mockBasket);

        // Act
        correlationService.completeResponse(correlationId, response);
        String result = correlationService.getPaymentIntentId(testBasketId);

        // Assert
        assertEquals(testPaymentIntentId, result);
        verify(basketRepository).findById(testBasketId);
        verify(basketRepository).save(mockBasket);
    }
    @Test
    void getPaymentIntentId_ShouldFallbackToRepository() {
        // Arrange
        Basket mockBasket = new Basket();
        mockBasket.setPaymentIntentId(testPaymentIntentId);
        when(basketRepository.findById(testBasketId)).thenReturn(Optional.of(mockBasket));

        // Act
        String result = correlationService.getPaymentIntentId(testBasketId);

        // Assert
        assertEquals(testPaymentIntentId, result);
        verify(basketRepository).findById(testBasketId);
    }

    @Test
    void getPaymentIntentId_WithUnknownBasket_ShouldThrowException() {
        // Arrange
        when(basketRepository.findById(testBasketId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> correlationService.getPaymentIntentId(testBasketId));
    }

    @Test
    void completeExceptionally_ShouldCompleteFutureWithException() {
        // Arrange
        correlationService.createCorrelation(testBasketId);
        CompletableFuture<PaymentIntentResponseEvent> future =
                correlationService.awaitResponse(testCorrelationId);
        RuntimeException exception = new RuntimeException("Test error");

        // Act
        correlationService.completeExceptionally(testCorrelationId, exception);

        // Assert
        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    void completeExceptionally_WithNullCorrelationId_ShouldLogError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Test error");

        // Act
        correlationService.completeExceptionally(null, exception);

        // Assert
        // Just verify no exceptions are thrown
    }

    @Test
    void completeExceptionally_WithUnknownCorrelationId_ShouldLogWarning() {
        // Arrange
        RuntimeException exception = new RuntimeException("Test error");

        // Act
        correlationService.completeExceptionally("unknown-correlation", exception);

        // Assert
        // Just verify no exceptions are thrown
    }

    @Test
    void updateBasketPaymentIntent_WithBasketNotFound_ShouldLogError() {
        // Arrange
        when(basketRepository.findById(testBasketId)).thenReturn(Optional.empty());

        // Act
        correlationService.updateBasketPaymentIntent(testBasketId, testPaymentIntentId);

        // Assert
        verify(basketRepository).findById(testBasketId);
        verify(basketRepository, never()).save(any());
    }
}

