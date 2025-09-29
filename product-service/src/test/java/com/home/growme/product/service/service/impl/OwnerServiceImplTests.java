package com.home.growme.product.service.service.impl;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.product.service.exception.OwnerAlreadyExistsException;
import com.home.growme.product.service.mapper.ProductMapper;
import com.home.growme.product.service.model.dto.OwnerDTO;
import com.home.growme.product.service.model.entity.Owner;
import com.home.growme.product.service.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceImplTests {

    private static final int MAX_OWNERS_LIMIT = 8;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private OwnerServiceImpl ownerService;

    private Owner owner1;
    private Owner owner2;
    private OwnerDTO ownerDTO1;
    private OwnerDTO ownerDTO2;
    private UserCreatedEvent userCreatedEvent;

    @BeforeEach
    void setUp() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        owner1 = new Owner();
        owner1.setOwnerId(userId1);
        owner1.setOwnerName("John Doe");
        owner1.setOwnerEmail("john@example.com");

        owner2 = new Owner();
        owner2.setOwnerId(userId2);
        owner2.setOwnerName("Jane Smith");
        owner2.setOwnerEmail("jane@example.com");

        ownerDTO1 = new OwnerDTO();
        ownerDTO1.setOwnerId(userId1);
        ownerDTO1.setOwnerName("John Doe");


        ownerDTO2 = new OwnerDTO();
        ownerDTO2.setOwnerId(userId2);
        ownerDTO2.setOwnerName("Jane Smith");


        userCreatedEvent = new UserCreatedEvent(
                UUID.randomUUID().toString(),
                "John Doe",
                "john@example.com"
        );

    }

    @Test
    void getAllOwners_shouldReturnListOfOwnerDTOsLimitedTo8() {
        // Arrange
        List<Owner> owners = Arrays.asList(owner1, owner2);
        when(ownerRepository.findAll()).thenReturn(owners);
        when(productMapper.mapOwnerToOwnerDTO(owner1)).thenReturn(ownerDTO1);
        when(productMapper.mapOwnerToOwnerDTO(owner2)).thenReturn(ownerDTO2);

        // Act
        List<OwnerDTO> result = ownerService.getAllOwners();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(ownerDTO1));
        assertTrue(result.contains(ownerDTO2));
        verify(ownerRepository).findAll();
        verify(productMapper, times(2)).mapOwnerToOwnerDTO(any(Owner.class));
    }

    @Test
    void getAllOwners_shouldLimitResultsToMaxLimit_whenMoreThanMaxLimitOwnersExist() {
        // Arrange
        List<Owner> owners = createMultipleOwners(10);
        when(ownerRepository.findAll()).thenReturn(owners);

        owners.stream()
                .limit(MAX_OWNERS_LIMIT)
                .forEach(owner ->
                        when(productMapper.mapOwnerToOwnerDTO(owner))
                                .thenReturn(new OwnerDTO()));

        // Act
        List<OwnerDTO> result = ownerService.getAllOwners();

        // Assert
        assertEquals(MAX_OWNERS_LIMIT, result.size());
        verify(ownerRepository).findAll();
        verify(productMapper, times(MAX_OWNERS_LIMIT)).mapOwnerToOwnerDTO(any(Owner.class));
    }

    @Test
    void createOwner_shouldSaveNewOwner_whenOwnerDoesNotExist() {
        // Arrange
        when(ownerRepository.existsById(any(UUID.class))).thenReturn(false);
        when(productMapper.mapUserCreatedEventToOwner(userCreatedEvent)).thenReturn(owner1);

        // Act
        ownerService.createOwner(userCreatedEvent);

        // Assert
        verify(ownerRepository).existsById(UUID.fromString(userCreatedEvent.getUserId()));
        verify(productMapper).mapUserCreatedEventToOwner(userCreatedEvent);
        verify(ownerRepository).save(owner1);
    }

    @Test
    void createOwner_shouldThrowOwnerAlreadyExistsException_whenOwnerExists() {

        // Arrange
        UUID userId = UUID.fromString(userCreatedEvent.getUserId());
        when(ownerRepository.existsById(userId)).thenReturn(true);

        // Act & Assert
        OwnerAlreadyExistsException exception = assertThrows(OwnerAlreadyExistsException.class, () -> {
            ownerService.createOwner(userCreatedEvent);
        });

        // Verify
        verify(ownerRepository).existsById(userId);
        verify(productMapper, never()).mapUserCreatedEventToOwner(any(UserCreatedEvent.class));
        verify(ownerRepository, never()).save(any(Owner.class));

        assertNotNull(exception);
    }

    @Test
    void getAllOwnersSortedByProductCount_shouldReturnSortedOwnerDTOs() {
        // Arrange
        Pageable pageable = PageRequest.of(0, MAX_OWNERS_LIMIT);
        when(ownerRepository.findAllOrderByProductCountDesc(pageable))
                .thenReturn(Arrays.asList(owner1, owner2));
        when(productMapper.mapOwnerToOwnerDTO(owner1)).thenReturn(ownerDTO1);
        when(productMapper.mapOwnerToOwnerDTO(owner2)).thenReturn(ownerDTO2);

        // Act
        List<OwnerDTO> result = ownerService.getAllOwnersSortedByProductCount();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(ownerDTO1));
        assertTrue(result.contains(ownerDTO2));
        verify(ownerRepository).findAllOrderByProductCountDesc(pageable);
        verify(productMapper, times(2)).mapOwnerToOwnerDTO(any(Owner.class));
    }

    @Test
    void getAllOwnersSortedByProductCount_shouldReturnEmptyList_whenNoOwnersExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, MAX_OWNERS_LIMIT);
        when(ownerRepository.findAllOrderByProductCountDesc(pageable))
                .thenReturn((List.of()));

        // Act
        List<OwnerDTO> result = ownerService.getAllOwnersSortedByProductCount();

        // Assert
        assertTrue(result.isEmpty());
        verify(ownerRepository).findAllOrderByProductCountDesc(pageable);
        verify(productMapper, never()).mapOwnerToOwnerDTO(any());
    }

    private List<Owner> createMultipleOwners(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Owner owner = new Owner();
                    owner.setOwnerId(UUID.randomUUID());
                    owner.setOwnerName("Owner " + i);
                    owner.setOwnerEmail("owner" + i + "@example.com");
                    return owner;
                })
                .collect(Collectors.toList());
    }
}