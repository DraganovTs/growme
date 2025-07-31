package com.home.order.service.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.order.service.model.dto.AddressDTO;
import com.home.order.service.model.dto.IOrderDto;
import com.home.order.service.model.dto.OrderDTO;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.model.entity.Order;
import com.home.order.service.model.enums.OrderStatus;
import com.home.order.service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
public class OrderControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private final String testCorrelationId = "test-correlation-id";
    private final String testBasketId = "basket-123";
    private final String testUserEmail = "test@example.com";
    private final UUID testOrderId = UUID.randomUUID();

    @Test
    void createOrUpdatePaymentIntent_ShouldReturnBasket() throws Exception {
        Basket mockBasket = new Basket();
        mockBasket.setId(testBasketId);

        when(orderService.createOrUpdatePaymentIntent(testBasketId, testCorrelationId))
                .thenReturn(mockBasket);

        mockMvc.perform(post("/api/orders/{basketId}", testBasketId)
                        .header("grow-me-correlation-id", testCorrelationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBasketId));

        verify(orderService).createOrUpdatePaymentIntent(testBasketId, testCorrelationId);
    }

    @Test
    void createOrUpdatePaymentIntent_ShouldReturn404_WhenBasketNotFound() throws Exception {
        when(orderService.createOrUpdatePaymentIntent(testBasketId, testCorrelationId))
                .thenReturn(null);

        mockMvc.perform(post("/api/orders/{basketId}", testBasketId)
                        .header("grow-me-correlation-id", testCorrelationId))
                .andExpect(status().isNotFound());

        verify(orderService).createOrUpdatePaymentIntent(testBasketId, testCorrelationId);
    }

    @Test
    void createOrUpdateOrder_ShouldReturnOrder() throws Exception {
        // Prepare test data
        OrderDTO expectedOrderDTO = OrderDTO.builder()
            .basketId(testBasketId)
            .deliveryMethodId(1)
            .shipToAddress( AddressDTO.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .street("Street")
                    .city("City")
                    .state("State")
                    .zipCode("12345")
                    .build())
            .userEmail("test@example.com")
            .build();
        
        Order mockOrder = Order.builder()
            .orderId(testOrderId)
            .buyerEmail(expectedOrderDTO.getUserEmail())
            .status(OrderStatus.PENDING)
            .build();

        // Configure mock
        when(orderService.createOrUpdateOrder(any(OrderDTO.class), anyString()))
            .thenReturn(mockOrder);

        // Perform test
        mockMvc.perform(post("/api/orders")
                .header("grow-me-correlation-id", testCorrelationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(expectedOrderDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(testOrderId.toString()))
            .andExpect(jsonPath("$.buyerEmail").value(expectedOrderDTO.getUserEmail()))
            .andExpect(jsonPath("$.status").value("PENDING"));

        // Verify service interaction
        ArgumentCaptor<OrderDTO> orderDTOCaptor = ArgumentCaptor.forClass(OrderDTO.class);
        verify(orderService).createOrUpdateOrder(orderDTOCaptor.capture(), eq(testCorrelationId));
        
        OrderDTO capturedDTO = orderDTOCaptor.getValue();
        assertEquals(expectedOrderDTO.getBasketId(), capturedDTO.getBasketId());
        assertEquals(expectedOrderDTO.getDeliveryMethodId(), capturedDTO.getDeliveryMethodId());
        assertEquals(expectedOrderDTO.getUserEmail(), capturedDTO.getUserEmail());
    }

    @Test
    void createOrUpdateOrder_ShouldReturn400_WhenInvalidInput() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .header("grow-me-correlation-id", testCorrelationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Empty request body
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrUpdateOrder(any(), any());
    }

    @Test
    void getAllOrdersForUser_ShouldReturnOrders() throws Exception {
        IOrderDto mockOrderDto = mock(IOrderDto.class);
        when(orderService.getAllOrdersForUser(testUserEmail))
                .thenReturn(List.of(mockOrderDto));

        mockMvc.perform(get("/api/orders/user/{userEmail}", testUserEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(orderService).getAllOrdersForUser(testUserEmail);
    }

    @Test
    void getAllOrdersForUser_ShouldReturn404_WhenNoOrders() throws Exception {
        when(orderService.getAllOrdersForUser(testUserEmail))
                .thenReturn(null);

        mockMvc.perform(get("/api/orders/user/{userEmail}", testUserEmail))
                .andExpect(status().isNotFound());

        verify(orderService).getAllOrdersForUser(testUserEmail);
    }

    @Test
    void getOrderDetails_ShouldReturnOrder() throws Exception {
        IOrderDto mockOrderDto = mock(IOrderDto.class);
        when(orderService.getOrderById(testOrderId))
                .thenReturn(mockOrderDto);

        mockMvc.perform(get("/api/orders/{orderId}", testOrderId))
                .andExpect(status().isOk());

        verify(orderService).getOrderById(testOrderId);
    }

    @Test
    void getOrderDetails_ShouldReturn404_WhenOrderNotFound() throws Exception {
        when(orderService.getOrderById(testOrderId))
                .thenReturn(null);

        mockMvc.perform(get("/api/orders/{orderId}", testOrderId))
                .andExpect(status().isNotFound());

        verify(orderService).getOrderById(testOrderId);
    }
}