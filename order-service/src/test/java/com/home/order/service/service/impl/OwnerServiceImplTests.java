package com.home.order.service.service.impl;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.order.service.exception.OwnerNotFoundException;
import com.home.order.service.model.entity.Owner;
import com.home.order.service.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@DisplayName("Owner Service Tests")
@ExtendWith(MockitoExtension.class)
public class OwnerServiceImplTests {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerServiceImpl ownerService;

    private Owner owner;
    private final String email = "owner@example.com";

    @BeforeEach
    void setUp() {
        owner = new Owner(
                UUID.randomUUID(),
                "John Doe",
                email,
                new ArrayList<>()
        );
    }

    @Test
    void shouldFindOwnerByEmailSuccessfully() {
        when(ownerRepository.findOwnerByOwnerEmail(email)).thenReturn(Optional.of(owner));

        Owner found = ownerService.findOwnerByEmail(email);

        assertEquals(owner.getOwnerEmail(), found.getOwnerEmail());
        assertEquals(owner.getOwnerName(), found.getOwnerName());
        verify(ownerRepository).findOwnerByOwnerEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenOwnerNotFoundByEmail() {
        when(ownerRepository.findOwnerByOwnerEmail(email)).thenReturn(Optional.empty());

        assertThrows(OwnerNotFoundException.class, () -> ownerService.findOwnerByEmail(email));
        verify(ownerRepository).findOwnerByOwnerEmail(email);
    }

    @Test
    void shouldCreateOwnerFromUserCreatedEvent() {
        UUID userId = UUID.randomUUID();
        UserCreatedEvent event = new UserCreatedEvent(
                userId.toString(),
                "Jane Doe",
                "jane@example.com"
        );

        ownerService.createOwner(event);

        verify(ownerRepository).save(argThat(saved ->
                saved.getOwnerId().equals(userId) &&
                        saved.getOwnerName().equals("Jane Doe") &&
                        saved.getOwnerEmail().equals("jane@example.com") &&
                        saved.getOrders().isEmpty()
        ));
    }
}
