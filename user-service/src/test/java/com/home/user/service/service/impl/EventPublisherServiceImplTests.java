package com.home.user.service.service.impl;

import com.home.growme.common.module.enums.EmailType;
import com.home.growme.common.module.events.EmailRequestEvent;
import com.home.growme.common.module.events.RoleAssignmentEvent;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;


import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventPublisherServiceImplTests {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private EventPublisherServiceImpl publisherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldPublishRoleAssignmentSuccessfully() {
        String userId = UUID.randomUUID().toString();
        String role = "buyer";

        CompletableFuture future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        assertDoesNotThrow(() -> publisherService.publishRoleAssignment(userId, role));

        verify(kafkaTemplate).send(eq(ROLE_ASSIGNMENT_TOPIC), eq(userId), any(RoleAssignmentEvent.class));
    }



    @Test
    void shouldPublishUserCreatedSuccessfully() {
        UserCreatedEvent event = new UserCreatedEvent(
                UUID.randomUUID().toString(),
                "username",
                "user@example.com"
        );

        CompletableFuture future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        assertDoesNotThrow(() -> publisherService.publishUserCreated(event));

        verify(kafkaTemplate).send(eq(USER_CREATE_TOPIC), eq(event.getUserId()), eq(event));
    }

    @Test
    void shouldPublishEmailRequestSuccessfully() {
        EmailRequestEvent event = new EmailRequestEvent(
                "user@example.com",
                EmailType.ACCOUNT_DELETION_CONFIRMATION
        );

        CompletableFuture future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), any())).thenReturn(future);

        assertDoesNotThrow(() -> publisherService.publishEmailRequest(event));

        verify(kafkaTemplate).send(eq(EMAIL_SEND_TOPIC), eq(event));
    }

    @Test
    void shouldLogAndNotThrowIfEmailPublishFails() {
        EmailRequestEvent event = new EmailRequestEvent(
                "fail@example.com",
                EmailType.ACCOUNT_COMPLETION_CONFIRMATION
        );

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka down"));

        when(kafkaTemplate.send(anyString(), any())).thenReturn(future);


        assertDoesNotThrow(() -> publisherService.publishEmailRequest(event));
    }
}