package com.home.user.service.service.impl;

import com.home.growme.common.module.enums.EmailType;
import com.home.growme.common.module.exceptions.EmailProcessingException;
import com.home.user.service.service.EventPublisherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class EmailServiceImplTests {
    @Mock
    private EventPublisherService eventPublisherService;

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendProductAddForSellConfirmation_shouldPublishCorrectEvent() {
        emailService.sendProductAddForSellConfirmation(testEmail);

        verify(eventPublisherService, times(1))
                .publishEmailRequest(argThat(event ->
                        event.getEmail().equals(testEmail) &&
                                event.getType() == EmailType.PRODUCT_ADD_CONFIRMATION));
    }

    @Test
    void sendAccountDeletionConfirmation_shouldPublishCorrectEvent() {
        emailService.sendAccountDeletionConfirmation(testEmail);

        verify(eventPublisherService, times(1))
                .publishEmailRequest(argThat(event ->
                        event.getEmail().equals(testEmail) &&
                                event.getType() == EmailType.ACCOUNT_DELETION_CONFIRMATION));
    }

    @Test
    void sendAccountCompletionConfirmation_shouldPublishCorrectEvent() {
        emailService.sendAccountCompletionConfirmation(testEmail);

        verify(eventPublisherService, times(1))
                .publishEmailRequest(argThat(event ->
                        event.getEmail().equals(testEmail) &&
                                event.getType() == EmailType.ACCOUNT_COMPLETION_CONFIRMATION));
    }

    @Test
    void sendAccountUpdateConfirmation_shouldPublishCorrectEvent() {
        emailService.sendAccountUpdateConfirmation(testEmail);

        verify(eventPublisherService, times(1))
                .publishEmailRequest(argThat(event ->
                        event.getEmail().equals(testEmail) &&
                                event.getType() == EmailType.ACCOUNT_UPDATE_CONFIRMATION));
    }

    @Test
    void publishEmailEvent_whenPublishThrowsException_shouldThrowEmailProcessingException() {
        doThrow(new RuntimeException("Some error")).when(eventPublisherService).publishEmailRequest(any());

        EmailProcessingException exception = assertThrows(EmailProcessingException.class, () ->
                emailService.sendProductAddForSellConfirmation(testEmail));

        assertEquals("Failed to process email request", exception.getMessage());
        verify(eventPublisherService, times(1)).publishEmailRequest(any());
    }
}
