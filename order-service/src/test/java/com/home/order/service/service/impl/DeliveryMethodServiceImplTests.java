package com.home.order.service.service.impl;
import com.home.order.service.mapper.DeliveryMethodMapper;
import com.home.order.service.model.dto.DeliveryMethodDTO;
import com.home.order.service.model.entity.DeliveryMethod;
import com.home.order.service.repository.DeliveryMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("Delivery Method Service Tests")
@ExtendWith(MockitoExtension.class)
public class DeliveryMethodServiceImplTests {
    @Mock
    private DeliveryMethodRepository deliveryMethodRepository;

    @Mock
    private DeliveryMethodMapper deliveryMethodMapper;

    @InjectMocks
    private DeliveryMethodServiceImpl deliveryMethodService;

    private DeliveryMethod deliveryMethod;
    private DeliveryMethodDTO deliveryMethodDTO;

    @BeforeEach
    void setUp() {
        deliveryMethod = DeliveryMethod.builder()
                .deliveryMethodId(1)
                .shortName("Fast")
                .description("Fast delivery")
                .deliveryTime("1-2 days")
                .price(BigDecimal.valueOf(9.99))
                .build();

        deliveryMethodDTO = DeliveryMethodDTO.builder()
                .deliveryMethodId(1)
                .shortName("Fast")
                .description("Fast delivery")
                .deliveryTime("1-2 days")
                .price(BigDecimal.valueOf(9.99))
                .build();
    }

    @Test
    void shouldReturnAllDeliveryMethods() {
        List<DeliveryMethod> deliveryMethods = List.of(deliveryMethod);
        List<DeliveryMethodDTO> deliveryMethodDTOs = List.of(deliveryMethodDTO);

        when(deliveryMethodRepository.findAll()).thenReturn(deliveryMethods);
        when(deliveryMethodMapper.mapDeliveryMethodToDeliveryMethodDTO(deliveryMethod)).thenReturn(deliveryMethodDTO);

        List<DeliveryMethodDTO> result = deliveryMethodService.getAllDeliveryMethods();

        assertEquals(deliveryMethodDTOs.size(), result.size());
        assertEquals(deliveryMethodDTO.getShortName(), result.get(0).getShortName());

        verify(deliveryMethodRepository).findAll();
        verify(deliveryMethodMapper).mapDeliveryMethodToDeliveryMethodDTO(deliveryMethod);
    }
}
