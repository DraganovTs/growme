package com.home.growme.product.service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.home.growme.common.module.dto.OrderItemDTO;
import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.product.service.config.EventMetrics;
import com.home.growme.product.service.exception.OwnerAlreadyExistsException;
import com.home.growme.product.service.model.enums.EventType;
import com.home.growme.product.service.service.OwnerService;
import com.home.growme.product.service.service.ProductService;
import com.home.growme.product.service.util.EventValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Product Event Handler Service Tests")
public class EventHandlerServiceImplTests {
    private OwnerService ownerService;
    private EventValidator eventValidator;
    private ProductService productService;
    private EventHandlerServiceImpl eventHandlerService;
    private ObjectMapper objectMapper;
    private EventMetrics metricsService;

    @BeforeEach
    void setUp() {
        ownerService = mock(OwnerService.class);
        eventValidator = mock(EventValidator.class);
        productService = mock(ProductService.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        metricsService = mock(EventMetrics.class);
        eventHandlerService = new EventHandlerServiceImpl(ownerService, eventValidator, productService,metricsService);

    }

    @Test
    @DisplayName("Should handle UserCreatedEvent successfully")
    void shouldHandleUserCreatedEventSuccessfully() {
        UserCreatedEvent event = new UserCreatedEvent("user123", "Alice", "alice@example.com");

        when(ownerService.existsByUserId(event.getUserId())).thenReturn(false);

        assertDoesNotThrow(() -> eventHandlerService.handleUserCreatedEvent(event));

        verify(eventValidator).validateUserCreatedEvent(event);
        verify(ownerService).existsByUserId(event.getUserId());
        verify(ownerService).createOwner(event);
        verify(metricsService).recordFailure(EventType.USER_CREATED);
    }

    @Test
    @DisplayName("Should handle OwnerAlreadyExistsException gracefully")
    void shouldHandleOwnerAlreadyExistsGracefully() {
        UserCreatedEvent event = new UserCreatedEvent("user123", "Bob", "bob@example.com");

        when(ownerService.existsByUserId(event.getUserId())).thenReturn(false);
        doThrow(new OwnerAlreadyExistsException("exists"))
                .when(ownerService).createOwner(event);

        assertDoesNotThrow(() -> eventHandlerService.handleUserCreatedEvent(event));

        verify(eventValidator).validateUserCreatedEvent(event);
        verify(ownerService).existsByUserId(event.getUserId());
        verify(ownerService).createOwner(event);
        verify(metricsService).recordFailure(EventType.USER_CREATED);
    }

    @Test
    @DisplayName("Should skip owner creation if owner already exists")
    void shouldSkipOwnerCreationIfOwnerExists() {
        UserCreatedEvent event = new UserCreatedEvent("user123", "Alice", "alice@example.com");

        when(ownerService.existsByUserId(event.getUserId())).thenReturn(true);

        assertDoesNotThrow(() -> eventHandlerService.handleUserCreatedEvent(event));

        verify(eventValidator).validateUserCreatedEvent(event);
        verify(ownerService).existsByUserId(event.getUserId());
        verify(ownerService, never()).createOwner(event);
        verify(metricsService).recordFailure(EventType.USER_CREATED);
    }

    @Test
    @DisplayName("Should throw on unexpected error in UserCreatedEvent")
    void shouldThrowOnUnexpectedErrorInUserCreatedEvent() {
        UserCreatedEvent event = new UserCreatedEvent("user123", "Carol", "carol@example.com");

        when(ownerService.existsByUserId(event.getUserId())).thenReturn(false);
        doThrow(new RuntimeException("unexpected")).when(ownerService).createOwner(event);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> eventHandlerService.handleUserCreatedEvent(event));

        assertEquals("unexpected", thrown.getMessage());
        verify(eventValidator).validateUserCreatedEvent(event);
        verify(ownerService).existsByUserId(event.getUserId());
        verify(ownerService).createOwner(event);
        verify(metricsService).recordFailure(EventType.USER_CREATED);
    }

    @Test
    @DisplayName("Should handle OrderCompletedEvent successfully")
    void shouldHandleOrderCompletedEventSuccessfully()  {
        OrderCompletedEvent event = new OrderCompletedEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "random@gmail.com",
                List.of(new OrderItemDTO(
                                UUID.randomUUID().toString(),
                                2,
                                BigDecimal.valueOf(10.11)),
                        new OrderItemDTO(
                                UUID.randomUUID().toString(),
                                3,
                                BigDecimal.valueOf(25.00))),
                BigDecimal.valueOf(35.11),
                Instant.now());

        assertDoesNotThrow(() -> eventHandlerService.OrderCompletedEvent(event));

        verify(eventValidator).validateOrderCompletedEvent(event);
        verify(productService).completeOrder(event);
        verify(metricsService).recordSuccess(EventType.ORDER_COMPLETED);
    }

    @Test
    @DisplayName("Should throw on unexpected error in OrderCompletedEvent")
    void shouldThrowOnUnexpectedErrorInOrderCompletedEvent() {
        OrderCompletedEvent event = new OrderCompletedEvent(
                UUID.randomUUID().toString()
                , UUID.randomUUID().toString()
                , "random@gmail.com",
                List.of(new OrderItemDTO(
                                UUID.randomUUID().toString(),
                                2,
                                BigDecimal.valueOf(10.11)),
                        new OrderItemDTO(
                                UUID.randomUUID().toString(),
                                3,
                                BigDecimal.valueOf(25.00))),
                BigDecimal.valueOf(35.11),
                Instant.now());
        doThrow(new RuntimeException("order error")).when(productService).completeOrder(event);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> eventHandlerService.OrderCompletedEvent(event));

        assertEquals("order error", thrown.getMessage());
        verify(eventValidator).validateOrderCompletedEvent(event);
        verify(productService).completeOrder(event);
    }

    @Test
    @DisplayName("Should validate UserCreatedEvent before processing")
    void shouldValidateUserCreatedEventBeforeProcessing() {
        UserCreatedEvent event = new UserCreatedEvent("user123", "Dave", "dave@example.com");
        doThrow(new IllegalArgumentException("validation failed"))
                .when(eventValidator).validateUserCreatedEvent(event);

        assertDoesNotThrow(() -> eventHandlerService.handleUserCreatedEvent(event));

        verify(eventValidator).validateUserCreatedEvent(event);
        verifyNoInteractions(ownerService);
        verify(metricsService).recordFailure(EventType.USER_CREATED);
    }

    @Test
    @DisplayName("Should validate OrderCompletedEvent before processing")
    void shouldValidateOrderCompletedEventBeforeProcessing() {
        OrderCompletedEvent event = new OrderCompletedEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "random@gmail.com",
                List.of(new OrderItemDTO(
                                UUID.randomUUID().toString(),
                                2,
                                BigDecimal.valueOf(10.11)),
                        new OrderItemDTO(
                                UUID.randomUUID().toString(),
                                3,
                                BigDecimal.valueOf(25.00))),
                BigDecimal.valueOf(35.11),
                Instant.now());
        doThrow(new IllegalArgumentException("order validation failed"))
                .when(eventValidator).validateOrderCompletedEvent(event);


        assertDoesNotThrow(() -> eventHandlerService.OrderCompletedEvent(event));

        verify(eventValidator).validateOrderCompletedEvent(event);
        verifyNoInteractions(productService);
        verify(metricsService).recordFailure(EventType.ORDER_COMPLETED);
    }

    @Test
    @DisplayName("Should record appropriate metrics for different scenarios")
    void shouldRecordAppropriateMetrics() {
        UserCreatedEvent event = new UserCreatedEvent("user123", "Test", "test@example.com");

        when(ownerService.existsByUserId(event.getUserId())).thenReturn(false);
        eventHandlerService.handleUserCreatedEvent(event);
        verify(metricsService).recordSuccess(EventType.USER_CREATED);

        reset(metricsService, ownerService);

        when(ownerService.existsByUserId(event.getUserId())).thenReturn(true);
        eventHandlerService.handleUserCreatedEvent(event);
        verify(metricsService).recordFailure(EventType.USER_CREATED);

        reset(metricsService, ownerService);

        when(ownerService.existsByUserId(event.getUserId())).thenReturn(false);
        doThrow(new RuntimeException("error")).when(ownerService).createOwner(event);

        assertThrows(RuntimeException.class,
                () -> eventHandlerService.handleUserCreatedEvent(event));
        verify(metricsService).recordFailure(EventType.ORDER_COMPLETED);
    }

    @Test
    @DisplayName("Should handle null event gracefully")
    void shouldHandleNullEventGracefully() {


        NullPointerException thrown = assertThrows(NullPointerException.class,
                () -> eventHandlerService.handleUserCreatedEvent(null));

        assertTrue(thrown.getMessage().contains("event"));
    }
}
