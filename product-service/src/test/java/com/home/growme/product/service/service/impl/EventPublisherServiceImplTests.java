package com.home.growme.product.service.service.impl;
import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.growme.common.module.events.ProductDeletionToUserEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;


import java.util.concurrent.CompletableFuture;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PRODUCT_ASSIGNMENT_TOPIC;
import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PRODUCT_DELETION_TOPIC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("Product Event Publisher Service Tests")
public class EventPublisherServiceImplTests {
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private EventPublisherServiceImpl eventPublisherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should publish product assignment successfully")
    void shouldPublishProductAssignmentSuccessfully() {
        // Arrange
        String userId = "user123";
        String productId = "prod456";

        CompletableFuture<SendResult<String, Object>> successfulFuture =
                CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(eq(PRODUCT_ASSIGNMENT_TOPIC), any(ProductAssignedToUserEvent.class)))
                .thenReturn(successfulFuture);

        // Act
        assertDoesNotThrow(() -> eventPublisherService.publishProductAssignment(userId, productId));

        // Assert
        ArgumentCaptor<ProductAssignedToUserEvent> eventCaptor = ArgumentCaptor.forClass(ProductAssignedToUserEvent.class);
        verify(kafkaTemplate).send(eq(PRODUCT_ASSIGNMENT_TOPIC), eventCaptor.capture());

        ProductAssignedToUserEvent capturedEvent = eventCaptor.getValue();
        assertEquals(userId, capturedEvent.getUserId());
        assertEquals(productId, capturedEvent.getProductId());
    }

    @Test
    @DisplayName("Should log error when product assignment publish fails")
    void shouldLogErrorWhenProductAssignmentPublishFails() {
        // Arrange
        String userId = "user123";
        String productId = "prod456";

        CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(eq(PRODUCT_ASSIGNMENT_TOPIC), any(ProductAssignedToUserEvent.class)))
                .thenReturn(failedFuture);

        // Act
        assertDoesNotThrow(() -> eventPublisherService.publishProductAssignment(userId, productId));

        // Assert
        ArgumentCaptor<ProductAssignedToUserEvent> eventCaptor = ArgumentCaptor.forClass(ProductAssignedToUserEvent.class);
        verify(kafkaTemplate).send(eq(PRODUCT_ASSIGNMENT_TOPIC), eventCaptor.capture());

        ProductAssignedToUserEvent capturedEvent = eventCaptor.getValue();
        assertEquals(userId, capturedEvent.getUserId());
        assertEquals(productId, capturedEvent.getProductId());
    }

    @Test
    @DisplayName("Should handle immediate Kafka send exception for product assignment")
    void shouldHandleImmediateKafkaSendExceptionForProductAssignment() {
        // Arrange
        String userId = "user123";
        String productId = "prod456";

        when(kafkaTemplate.send(eq(PRODUCT_ASSIGNMENT_TOPIC), any(ProductAssignedToUserEvent.class)))
                .thenThrow(new RuntimeException("Immediate Kafka error"));

        // Act
        assertDoesNotThrow(() -> eventPublisherService.publishProductAssignment(userId, productId));

        // Assert
        ArgumentCaptor<ProductAssignedToUserEvent> eventCaptor = ArgumentCaptor.forClass(ProductAssignedToUserEvent.class);
        verify(kafkaTemplate).send(eq(PRODUCT_ASSIGNMENT_TOPIC), eventCaptor.capture());

        ProductAssignedToUserEvent capturedEvent = eventCaptor.getValue();
        assertEquals(userId, capturedEvent.getUserId());
        assertEquals(productId, capturedEvent.getProductId());
    }

    @Test
    @DisplayName("Should publish product deletion successfully")
    void shouldPublishProductDeletionSuccessfully() {
        // Arrange
        String productId = "prod789";
        String ownerId = "user456";

        CompletableFuture<SendResult<String, Object>> successfulFuture =
                CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(eq(PRODUCT_DELETION_TOPIC), any(ProductDeletionToUserEvent.class)))
                .thenReturn(successfulFuture);

        // Act
        assertDoesNotThrow(() -> eventPublisherService.publishProductDeletion(productId, ownerId));

        // Assert
        ArgumentCaptor<ProductDeletionToUserEvent> eventCaptor = ArgumentCaptor.forClass(ProductDeletionToUserEvent.class);
        verify(kafkaTemplate).send(eq(PRODUCT_DELETION_TOPIC), eventCaptor.capture());

        ProductDeletionToUserEvent capturedEvent = eventCaptor.getValue();
        assertEquals(ownerId, capturedEvent.getUserId());
        assertEquals(productId, capturedEvent.getProductId());
    }

    @Test
    @DisplayName("Should log error when product deletion publish fails")
    void shouldLogErrorWhenProductDeletionPublishFails() {
        // Arrange
        String productId = "prod789";
        String ownerId = "user456";

        CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(eq(PRODUCT_DELETION_TOPIC), any(ProductDeletionToUserEvent.class)))
                .thenReturn(failedFuture);

        // Act
        assertDoesNotThrow(() -> eventPublisherService.publishProductDeletion(productId, ownerId));

        // Assert
        ArgumentCaptor<ProductDeletionToUserEvent> eventCaptor = ArgumentCaptor.forClass(ProductDeletionToUserEvent.class);
        verify(kafkaTemplate).send(eq(PRODUCT_DELETION_TOPIC), eventCaptor.capture());

        ProductDeletionToUserEvent capturedEvent = eventCaptor.getValue();
        assertEquals(ownerId, capturedEvent.getUserId());
        assertEquals(productId, capturedEvent.getProductId());
    }


    @Test
    @DisplayName("Should handle immediate Kafka send exception for product deletion")
    void shouldHandleImmediateKafkaSendExceptionForProductDeletion() {
        // Arrange
        String productId = "prod789";
        String ownerId = "user456";
        ProductDeletionToUserEvent expectedEvent = new ProductDeletionToUserEvent(ownerId, productId);

        when(kafkaTemplate.send(eq(PRODUCT_DELETION_TOPIC), any(ProductDeletionToUserEvent.class)))
                .thenReturn(null);

        // Act
        assertDoesNotThrow(() -> eventPublisherService.publishProductDeletion(productId, ownerId));

        // Assert
        verify(kafkaTemplate).send(eq(PRODUCT_DELETION_TOPIC), argThat(event -> {
            if (event instanceof ProductDeletionToUserEvent) {
                ProductDeletionToUserEvent e = (ProductDeletionToUserEvent) event;
                return ownerId.equals(e.getUserId()) && productId.equals(e.getProductId());
            }
            return false;
        }));
    }

    @Test
    @DisplayName("Should construct correct ProductAssignedToUserEvent")
    void shouldConstructCorrectProductAssignedToUserEvent() {
        // Arrange
        String userId = "user123";
        String productId = "prod456";

        // Act
        eventPublisherService.publishProductAssignment(userId, productId);

        // Assert
        ArgumentCaptor<ProductAssignedToUserEvent> eventCaptor = ArgumentCaptor.forClass(ProductAssignedToUserEvent.class);
        verify(kafkaTemplate).send(eq(PRODUCT_ASSIGNMENT_TOPIC), eventCaptor.capture());

        ProductAssignedToUserEvent capturedEvent = eventCaptor.getValue();
        assertEquals(userId, capturedEvent.getUserId());
        assertEquals(productId, capturedEvent.getProductId());
    }

    @Test
    @DisplayName("Should construct correct ProductDeletionToUserEvent")
    void shouldConstructCorrectProductDeletionToUserEvent() {
        // Arrange
        String productId = "prod789";
        String ownerId = "user456";

        // Act
        eventPublisherService.publishProductDeletion(productId, ownerId);

        // Assert
        ArgumentCaptor<ProductDeletionToUserEvent> eventCaptor = ArgumentCaptor.forClass(ProductDeletionToUserEvent.class);
        verify(kafkaTemplate).send(eq(PRODUCT_DELETION_TOPIC), eventCaptor.capture());

        ProductDeletionToUserEvent capturedEvent = eventCaptor.getValue();
        assertEquals(ownerId, capturedEvent.getUserId());
        assertEquals(productId, capturedEvent.getProductId());
    }
}
