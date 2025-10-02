package com.home.user.service.service.impl;

import com.home.growme.common.module.dto.OrderItemDTO;
import com.home.growme.common.module.events.*;
import com.home.user.service.config.EventMetrics;
import com.home.user.service.exception.UserAlreadyExistException;
import com.home.user.service.model.entity.User;
import com.home.user.service.repository.UserRepository;
import com.home.user.service.service.EventPublisherService;
import com.home.user.service.service.UserCommandService;
import com.home.user.service.util.EventValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventHandlerServiceImplTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EventPublisherService eventPublisherService;

    @Mock
    private EventValidator eventValidator;

    @Mock
    private UserCommandService userCommandService;

    @InjectMocks
    private EventHandlerServiceImpl eventHandlerService;

    @Mock
    private EventMetrics eventMetrics;

    private final UUID userId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();
    private final String userIdString = userId.toString();
    private final String productIdString = productId.toString();
    private final String orderIdString = orderId.toString();
    private final String buyerEmail = "buyer@example.com";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---- handleRoleAssignmentResult ----

    @Test
    void handleRoleAssignmentResult_UserExists_PublishesUserCreatedEvent() {
        RoleAssignmentResult result = new RoleAssignmentResult("correlationId",userIdString,true);


        User user = new User();
        user.setUserId(userId);
        user.setUsername("testUser");
        user.setEmail("test@example.com");


        when(userRepository.existsById(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        eventHandlerService.handleRoleAssignmentResult(result);

        ArgumentCaptor<UserCreatedEvent> captor = ArgumentCaptor.forClass(UserCreatedEvent.class);
        verify(eventPublisherService).publishUserCreated(captor.capture());

        UserCreatedEvent publishedEvent = captor.getValue();
        assertEquals(userIdString, publishedEvent.getUserId());
        assertEquals(user.getUsername(), publishedEvent.getUserName());
        assertEquals(user.getEmail(), publishedEvent.getUserEmail());
    }

    @Test
    void handleRoleAssignmentResult_UserNotFound_ThrowsException() {
        RoleAssignmentResult result = new RoleAssignmentResult("corelationId",userIdString,true);



        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserAlreadyExistException thrown = assertThrows(UserAlreadyExistException.class, () ->
                eventHandlerService.handleRoleAssignmentResult(result));

        assertTrue(thrown.getMessage().contains(userIdString));
        verify(eventPublisherService, never()).publishUserCreated(any());
    }

    // ---- handleProductAssignment ----

    @Test
    void handleProductAssignment_ValidEvent_CallsAddOwnedProduct() {
        ProductAssignedToUserEvent event = new ProductAssignedToUserEvent(userIdString,productIdString);


        doNothing().when(userCommandService).addOwnedProduct(event.getUserId(), event.getProductId());
        doNothing().when(eventValidator).validateProductAssignment(event);

        assertDoesNotThrow(() -> eventHandlerService.handleProductAssignment(event));

        verify(userCommandService).addOwnedProduct(event.getUserId(), event.getProductId());
    }

    // ---- handleProductDeletion ----

    @Test
    void handleProductDeletion_ValidEvent_CallsDeleteOwnedProduct() {
        ProductDeletionToUserEvent event = new ProductDeletionToUserEvent(userIdString,productIdString);


        doNothing().when(userCommandService).deleteOwnedProduct(event.getUserId(), event.getProductId());
        doNothing().when(eventValidator).validateProductDeletion(event);

        assertDoesNotThrow(() -> eventHandlerService.handleProductDeletion(event));

        verify(userCommandService).deleteOwnedProduct(event.getUserId(), event.getProductId());
    }

    // ---- orderCompleted ----

    @Test
    void orderCompleted_ValidEvent_CallsAddOwnerOrder() {
        List<OrderItemDTO> items = List.of();
        OrderCompletedEvent event = new OrderCompletedEvent(
                orderIdString,
                userIdString,
                buyerEmail,
                items,
                new BigDecimal("99.99"),
                Instant.now()
        );

        doNothing().when(eventValidator).validateOrderCompleted(event);
        doNothing().when(userCommandService).addOwnerOrder(event.getOrderUserId(), event.getOrderId());

        assertDoesNotThrow(() -> eventHandlerService.orderCompleted(event));

        verify(userCommandService).addOwnerOrder(event.getOrderUserId(), event.getOrderId());
    }

    // ---- Exception tests ----

    @Test
    void handleProductAssignment_ValidatorThrows_ExceptionPropagated() {
        List<OrderItemDTO> items = List.of(); // or mock some DTOs
        ProductAssignedToUserEvent event = new ProductAssignedToUserEvent(userIdString,productIdString);
        doThrow(new RuntimeException("Validation failed")).when(eventValidator).validateProductAssignment(event);

        assertThrows(RuntimeException.class, () -> eventHandlerService.handleProductAssignment(event));
        verify(userCommandService, never()).addOwnedProduct(anyString(), anyString());
    }

    @Test
    void handleProductDeletion_ValidatorThrows_ExceptionPropagated() {
        ProductDeletionToUserEvent event = new ProductDeletionToUserEvent(userIdString,productIdString);
        doThrow(new RuntimeException("Validation failed")).when(eventValidator).validateProductDeletion(event);

        assertThrows(RuntimeException.class, () -> eventHandlerService.handleProductDeletion(event));
        verify(userCommandService, never()).deleteOwnedProduct(anyString(), anyString());
    }

    @Test
    void orderCompleted_ValidatorThrows_ExceptionPropagated() {
        List<OrderItemDTO> items = List.of();
        OrderCompletedEvent event = new OrderCompletedEvent(
                orderIdString,
                userIdString,
                buyerEmail,
                items,
                new BigDecimal("99.99"),
                Instant.now()
        );
        doThrow(new RuntimeException("Validation failed")).when(eventValidator).validateOrderCompleted(event);

        assertThrows(RuntimeException.class, () -> eventHandlerService.orderCompleted(event));
        verify(userCommandService, never()).addOwnerOrder(anyString(), anyString());
    }
}
