package com.home.order.service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.home.growme.common.module.events.PaymentFailureEvent;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.order.service.config.EventMetrics;
import com.home.order.service.exception.PaymentFailedException;
import com.home.order.service.model.enums.OrderStatus;
import com.home.order.service.service.CorrelationService;
import com.home.order.service.service.OrderService;
import com.home.order.service.service.OwnerService;
import com.home.order.service.util.EventValidator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Event Handler Service Tests")
public class EventHandlerServiceImplTests {
    private CorrelationService correlationService;
    private ObjectMapper objectMapper;
    private OwnerService ownerService;
    private OrderService orderService;
    private EventHandlerServiceImpl eventHandlerService;
    private EventMetrics eventMetrics;
    private EventValidator eventValidator;

    @BeforeEach
    void setUp() {
        correlationService = mock(CorrelationService.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ownerService = mock(OwnerService.class);
        orderService = mock(OrderService.class);
        eventValidator = mock(EventValidator.class);
        eventMetrics = mock(EventMetrics.class);
        eventHandlerService = new EventHandlerServiceImpl(correlationService, objectMapper, ownerService, orderService,
                eventMetrics, eventValidator);
    }

    @Test
    void shouldHandlePaymentIntentResponseSuccessfully() throws JsonProcessingException {
        PaymentIntentResponseEvent responseEvent =
                new PaymentIntentResponseEvent("correlation-xyz", "payment123", "secret-456", "succeeded");


        String json = objectMapper.writeValueAsString(responseEvent);
        Object value = objectMapper.readValue(json, Object.class);

        ConsumerRecord<String, Object> record = new ConsumerRecord<>("topic", 0, 0L, "key", value);

        eventHandlerService.handlePaymentResponse(record);

        verify(correlationService).completeResponse(eq("correlation-xyz"), any(PaymentIntentResponseEvent.class));
        verify(orderService).updateOrderStatusByPaymentIntentId("payment123", OrderStatus.PAYMENT_RECEIVED);
    }

    @Test
    void shouldNotProcessPaymentIntentResponseWithoutCorrelationId() {
        PaymentIntentResponseEvent responseEvent = new PaymentIntentResponseEvent(null, "payment123", "secret-456", "succeeded");
        ;
        ConsumerRecord<String, Object> record = new ConsumerRecord<>("topic", 1, 1L, "key", responseEvent);

        eventHandlerService.handlePaymentResponse(record);

        verify(correlationService, never()).completeResponse(any(), any());
        verify(orderService, never()).updateOrderStatusByPaymentIntentId(any(), any());
    }

    @Test
    void shouldHandlePaymentFailureEventSuccessfully() throws JsonProcessingException {
        PaymentFailureEvent failureEvent = new PaymentFailureEvent("correlation-xyz", "payment123", "secret-456", "succeeded");

        String jsonValue = objectMapper.writeValueAsString(failureEvent);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", jsonValue);

        eventHandlerService.handlePaymentFailure(record);

        ArgumentCaptor<PaymentFailedException> captor = ArgumentCaptor.forClass(PaymentFailedException.class);
        verify(correlationService).completeExceptionally(eq("correlation-xyz"), captor.capture());
        assertEquals("secret-456", captor.getValue().getMessage());
    }

    @Test
    void shouldHandleJsonProcessingExceptionGracefully() {
        String invalidJson = "{ invalid_json }";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", invalidJson);


        assertDoesNotThrow(() -> eventHandlerService.handlePaymentFailure(record));

        verify(correlationService, never()).completeExceptionally(any(), any());
    }

    @Test
    void shouldHandleUserCreatedEventSuccessfully() {
        UserCreatedEvent event = new UserCreatedEvent("user123", "Alice", "alice@example.com");

        assertDoesNotThrow(() -> eventHandlerService.handleUserCreatedEvent(event));
        verify(ownerService).createOwner(event);
    }

    @Test
    void shouldHandleOwnerAlreadyExistsGracefully() {
        UserCreatedEvent event = new UserCreatedEvent("user123", "Bob", "bob@example.com");
        doThrow(new com.home.order.service.exception.OwnerAlreadyExistsException("exists"))
                .when(ownerService).createOwner(event);


        assertDoesNotThrow(() -> eventHandlerService.handleUserCreatedEvent(event));

        verify(ownerService).createOwner(event);
    }

    @Test
    void shouldThrowOnUnexpectedErrorInUserCreatedEvent() {
        UserCreatedEvent event = new UserCreatedEvent("user123", "Carol", "carol@example.com");
        doThrow(new RuntimeException("unexpected")).when(ownerService).createOwner(event);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> eventHandlerService.handleUserCreatedEvent(event));

        assertEquals("unexpected", thrown.getMessage());
        verify(ownerService).createOwner(event);
    }
}
