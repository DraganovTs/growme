package com.home.growme.produt.service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.home.growme.common.module.dto.OrderItemDTO;
import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.produt.service.exception.OwnerAlreadyExistsException;
import com.home.growme.produt.service.service.OwnerService;
import com.home.growme.produt.service.service.ProductService;
import com.home.growme.produt.service.util.EventValidator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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

    @BeforeEach
    void setUp() {
        ownerService = mock(OwnerService.class);
        eventValidator = mock(EventValidator.class);
        productService = mock(ProductService.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        eventHandlerService = new EventHandlerServiceImpl(ownerService, eventValidator, productService);

    }

    @Test
    @DisplayName("Should handle UserCreatedEvent successfully")
    void shouldHandleUserCreatedEventSuccessfully() {
        UserCreatedEvent event = new UserCreatedEvent("user123", "Alice", "alice@example.com");

        assertDoesNotThrow(() -> eventHandlerService.handleUserCreatedEvent(event));

        verify(eventValidator).validateUserCreatedEvent(event);
        verify(ownerService).createOwner(event);
    }

    @Test
    @DisplayName("Should handle OwnerAlreadyExistsException gracefully")
    void shouldHandleOwnerAlreadyExistsGracefully() {
        UserCreatedEvent event = new UserCreatedEvent("user123", "Bob", "bob@example.com");
        doThrow(new OwnerAlreadyExistsException("exists"))
                .when(ownerService).createOwner(event);

        assertDoesNotThrow(() -> eventHandlerService.handleUserCreatedEvent(event));

        verify(eventValidator).validateUserCreatedEvent(event);
        verify(ownerService).createOwner(event);
    }

    @Test
    @DisplayName("Should throw on unexpected error in UserCreatedEvent")
    void shouldThrowOnUnexpectedErrorInUserCreatedEvent() {
        UserCreatedEvent event = new UserCreatedEvent("user123", "Carol", "carol@example.com");
        doThrow(new RuntimeException("unexpected")).when(ownerService).createOwner(event);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> eventHandlerService.handleUserCreatedEvent(event));

        assertEquals("unexpected", thrown.getMessage());
        verify(eventValidator).validateUserCreatedEvent(event);
        verify(ownerService).createOwner(event);
    }

    @Test
    @DisplayName("Should handle OrderCompletedEvent successfully")
    void shouldHandleOrderCompletedEventSuccessfully() throws JsonProcessingException {
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
        String json = objectMapper.writeValueAsString(event);
        Object value = objectMapper.readValue(json, Object.class);
        ConsumerRecord<String, Object> record = new ConsumerRecord<>("topic", 0, 0L, "key", value);

        assertDoesNotThrow(() -> eventHandlerService.OrderCompletedEvent(event));

        verify(eventValidator).validateOrderCompletedEvent(event);
        verify(productService).completeOrder(event);
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

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> eventHandlerService.handleUserCreatedEvent(event));

        assertEquals("validation failed", thrown.getMessage());
        verify(eventValidator).validateUserCreatedEvent(event);
        verifyNoInteractions(ownerService);
    }

    @Test
    @DisplayName("Should validate OrderCompletedEvent before processing")
    void shouldValidateOrderCompletedEventBeforeProcessing() {
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
        doThrow(new IllegalArgumentException("order validation failed"))
                .when(eventValidator).validateOrderCompletedEvent(event);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> eventHandlerService.OrderCompletedEvent(event));

        assertEquals("order validation failed", thrown.getMessage());
        verify(eventValidator).validateOrderCompletedEvent(event);
        verifyNoInteractions(productService);
    }
}
