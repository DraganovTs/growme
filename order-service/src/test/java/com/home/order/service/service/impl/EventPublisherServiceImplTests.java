package com.home.order.service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.growme.common.module.events.PaymentIntentRequestEvent;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.order.service.config.PaymentProperties;
import com.home.order.service.feign.UserServiceClient;
import com.home.order.service.service.CorrelationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PAYMENT_INTENT_REQUESTS_TOPIC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Event Publisher Service Tests")
public class EventPublisherServiceImplTests {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private CorrelationService correlationService;

    @Mock
    private PaymentProperties paymentProperties;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EventPublisherServiceImpl eventPublisherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);


        when(paymentProperties.getResponseTimeout()).thenReturn(Duration.ofSeconds(5));

        eventPublisherService = new EventPublisherServiceImpl(
                kafkaTemplate,
                correlationService,
                paymentProperties,
                objectMapper,
                userServiceClient
        );
    }

    @Test
    void shouldSendCreatePaymentIntentSuccessfully() throws Exception {
        // Arrange
        String basketId = "basket-123";
        BigDecimal amount = BigDecimal.valueOf(99.99);
        String correlationId = "corr-xyz";

        PaymentIntentResponseEvent responseEvent = new PaymentIntentResponseEvent(
                correlationId, "intent-456", "secret-abc", "succeeded"
        );
        CompletableFuture<PaymentIntentResponseEvent> responseFuture = CompletableFuture.completedFuture(responseEvent);

        // Mock correlation service
        when(correlationService.createCorrelation(basketId)).thenReturn(correlationId);
        when(correlationService.awaitResponse(correlationId)).thenReturn(responseFuture);
        when(paymentProperties.getResponseTimeout()).thenReturn(Duration.ofSeconds(2));

        // Mock object mapper
        when(objectMapper.writeValueAsString(any(PaymentIntentRequestEvent.class))).thenReturn("json-event");

        // Create a completed future for Kafka send result
        CompletableFuture<SendResult<String, Object>> kafkaFuture = CompletableFuture.completedFuture(mock(SendResult.class));

        // Mock KafkaTemplate to return the completed future
        when(kafkaTemplate.send(eq(PAYMENT_INTENT_REQUESTS_TOPIC), anyString()))
                .thenReturn(kafkaFuture);

        // Act
        CompletableFuture<PaymentIntentResponseEvent> result = eventPublisherService.sendCreatePaymentIntent(basketId, amount);

        // Assert
        assertNotNull(result);
        PaymentIntentResponseEvent event = result.get();
        assertEquals("intent-456", event.getPaymentIntentId());
        assertEquals("secret-abc", event.getClientSecret());

        // Verify interactions
        verify(correlationService).createCorrelation(basketId);
        verify(correlationService).awaitResponse(correlationId);
        verify(kafkaTemplate).send(eq(PAYMENT_INTENT_REQUESTS_TOPIC), anyString());
    }

    @Test
    void shouldHandleJsonProcessingExceptionAndCompleteExceptionally() throws Exception {
        // Arrange
        String basketId = "basket-bad";
        BigDecimal amount = BigDecimal.valueOf(88.88);
        String correlationId = "bad-corr";

        when(correlationService.createCorrelation(basketId)).thenReturn(correlationId);
        when(correlationService.awaitResponse(correlationId)).thenReturn(new CompletableFuture<>());

        ObjectMapper brokenMapper = mock(ObjectMapper.class);
        EventPublisherServiceImpl brokenService = new EventPublisherServiceImpl(
                kafkaTemplate, correlationService, paymentProperties, brokenMapper, userServiceClient
        );

        when(brokenMapper.writeValueAsString(any(PaymentIntentRequestEvent.class)))
                .thenThrow(new JsonProcessingException("Boom") {});

        // Act
        brokenService.sendCreatePaymentIntent(basketId, amount);

        // Assert
        verify(correlationService).completeExceptionally(eq(correlationId), any(EventPublishingException.class));
    }


}
