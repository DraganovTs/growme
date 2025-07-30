package com.home.order.service.service.impl;

import com.home.growme.common.module.dto.BasketItemDTO;
import com.home.growme.common.module.dto.ProductValidationResult;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.order.service.exception.*;
import com.home.order.service.feign.ProductServiceClient;
import com.home.order.service.mapper.BasketMapper;
import com.home.order.service.mapper.OrderMapper;
import com.home.order.service.model.dto.AddressDTO;
import com.home.order.service.model.dto.IOrderDto;
import com.home.order.service.model.dto.OrderDTO;
import com.home.order.service.model.entity.*;
import com.home.order.service.model.enums.OrderStatus;
import com.home.order.service.repository.BasketRepository;
import com.home.order.service.repository.DeliveryMethodRepository;
import com.home.order.service.repository.OrderRepository;
import com.home.order.service.service.EventPublisherService;
import com.home.order.service.service.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Order Service Tests")
@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTests {
    private static final String TEST_BASKET_ID = "test-basket-id";
    private static final String TEST_CORRELATION_ID = "test-correlation-id";
    private static final String TEST_PAYMENT_INTENT_ID = "test-payment-intent-id";
    private static final String TEST_USER_EMAIL = "test@example.com";
    private static final BigDecimal TEST_ITEM_PRICE = BigDecimal.valueOf(10.99);
    private static final BigDecimal TEST_SHIPPING_PRICE = BigDecimal.valueOf(5.99);
    private static final int TEST_ITEM_QUANTITY = 2;
    private static final int TEST_DELIVERY_METHOD_ID = 1;

    @Mock private BasketRepository basketRepository;
    @Mock private DeliveryMethodRepository deliveryMethodRepository;
    @Mock private ProductServiceClient productServiceClient;
    @Mock private BasketMapper basketMapper;
    @Mock private EventPublisherService eventPublisherService;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private OwnerService ownerService;
    @InjectMocks private OrderServiceImpl orderService;

    private UUID orderId;
    private Basket testBasket;
    private OrderDTO testOrderDTO;
    private Order testOrder;
    private Owner testOwner;
    private DeliveryMethod testDeliveryMethod;
    private String basketId;
    private String correlationId;
    private String paymentIntentId;
    private String userEmail;

    @BeforeEach
    void setup() {
        orderId = UUID.randomUUID();
        testBasket = createTestBasket();
        testDeliveryMethod = createTestDeliveryMethod();
        testOrderDTO = createTestOrderDTO();
        testOrder = createTestOrder();
        testOwner = createTestOwner();
        basketId = TEST_BASKET_ID;
        correlationId = TEST_CORRELATION_ID;
        paymentIntentId = TEST_PAYMENT_INTENT_ID;
        userEmail = TEST_USER_EMAIL;
        orderService = new OrderServiceImpl(
                basketRepository,
                deliveryMethodRepository,
                productServiceClient,
                basketMapper,
                eventPublisherService,
                orderRepository,
                orderMapper,
                ownerService
        );
    }

    private Basket createTestBasket() {
        BasketItem basketItem = BasketItem.builder()
                .productId(UUID.randomUUID())
                .quantity(TEST_ITEM_QUANTITY)
                .price(TEST_ITEM_PRICE)
                .build();

        return Basket.builder()
                .id(TEST_BASKET_ID)
                .items(List.of(basketItem))
                .deliveryMethodId(TEST_DELIVERY_METHOD_ID)
                .build();
    }

    private DeliveryMethod createTestDeliveryMethod() {
        return DeliveryMethod.builder()
                .deliveryMethodId(TEST_DELIVERY_METHOD_ID)
                .price(TEST_SHIPPING_PRICE)
                .build();
    }

    private OrderDTO createTestOrderDTO() {
        AddressDTO addressDTO = AddressDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .street("123 Main St")
                .city("Anytown")
                .state("CA")
                .zipCode("12345")
                .build();

        return OrderDTO.builder()
                .basketId(TEST_BASKET_ID)
                .userEmail(TEST_USER_EMAIL)
                .deliveryMethodId(TEST_DELIVERY_METHOD_ID)
                .shipToAddress(addressDTO)
                .build();
    }

    private Order createTestOrder() {
        Address address = createTestAddress();
        OrderItem orderItem = createTestOrderItem(null);

        Order order = Order.builder()
                .orderId(orderId)
                .buyerEmail(TEST_USER_EMAIL)
                .shipToAddress(address)
                .orderItems(List.of(orderItem))
                .deliveryMethod(testDeliveryMethod)
                .subTotal(BigDecimal.valueOf(21.98))
                .status(OrderStatus.PENDING)
                .paymentIntentId(TEST_PAYMENT_INTENT_ID)
                .orderDate(Instant.now())
                .build();


        orderItem.setOrder(order);
        return order;
    }

    private Address createTestAddress() {
        return Address.builder()
                .firstName("John")
                .lastName("Doe")
                .street("123 Main St")
                .city("Anytown")
                .state("CA")
                .zipCode("12345")
                .build();
    }

    private OrderItem createTestOrderItem(Order order) {
        ProductItemOrdered productItemOrdered = createTestProductItemOrdered();
        return OrderItem.builder()
                .orderItemId(UUID.randomUUID())
                .itemOrdered(productItemOrdered)
                .quantity(TEST_ITEM_QUANTITY)
                .price(TEST_ITEM_PRICE)
                .order(order)
                .build();
    }

    private Owner createTestOwner() {
        return Owner.builder()
                .ownerId(UUID.randomUUID())
                .ownerName("ownerName")
                .ownerEmail(TEST_USER_EMAIL)
                .orders(new ArrayList<>(List.of(testOrder)))
                .build();
    }

    private ProductItemOrdered createTestProductItemOrdered() {
        return ProductItemOrdered.builder()
                .productItemId(UUID.randomUUID())
                .productName("product-1")
                .imageUrl("image-1")
                .build();
    }


    @Nested
    @DisplayName("Create or Update Payment Intent")
    class CreateOrUpdatePaymentIntentTests {

        @Test
        @DisplayName("Should create new payment intent successfully")
        void shouldCreateNewPaymentIntentSuccessfully() {
            testBasket.setPaymentIntentId(null);

            // 1. Mock repository
            when(basketRepository.findById(basketId)).thenReturn(Optional.of(testBasket));
            when(basketRepository.save(any())).thenReturn(testBasket);

            // 2. Mock productServiceClient (THIS WAS MISSING)
            when(productServiceClient.validateBasketItems(any(), any()))
                    .thenReturn(ResponseEntity.ok(List.of(
                            new ProductValidationResult(
                                    testBasket.getItems().get(0).getProductId(),
                                    true,
                                    "Valid"
                            )
                    )));

            // 3. Mock delivery method
            when(deliveryMethodRepository.findById(TEST_DELIVERY_METHOD_ID))
                    .thenReturn(Optional.of(testDeliveryMethod));

            // 4. Mock payment service response
            PaymentIntentResponseEvent response = new PaymentIntentResponseEvent(
                    correlationId, paymentIntentId, "client-secret", "completed"
            );
            when(eventPublisherService.sendCreatePaymentIntent(basketId, BigDecimal.valueOf(27.97)))
                    .thenReturn(CompletableFuture.completedFuture(response));

            // 5. Call method under test
            Basket result = orderService.createOrUpdatePaymentIntent(basketId, correlationId);

            // 6. Assertions
            assertEquals(paymentIntentId, result.getPaymentIntentId());
            assertEquals("client-secret", result.getClientSecret());

            // 7. Verify
            verify(basketRepository).save(testBasket);
        }

        @Test
        @DisplayName("Should update existing payment intent successfully")
        void shouldUpdateExistingPaymentIntentSuccessfully() {
            testBasket.setPaymentIntentId(paymentIntentId);

            when(basketRepository.findById(basketId)).thenReturn(Optional.of(testBasket));
            when(basketRepository.save(any())).thenReturn(testBasket);

            when(productServiceClient.validateBasketItems(any(), any()))
                    .thenReturn(ResponseEntity.ok(List.of(
                            new ProductValidationResult(
                                    testBasket.getItems().get(0).getProductId(),
                                    true,
                                    "Valid"
                            )
                    )));

            when(deliveryMethodRepository.findById(TEST_DELIVERY_METHOD_ID))
                    .thenReturn(Optional.of(testDeliveryMethod));

            PaymentIntentResponseEvent response = new PaymentIntentResponseEvent(
                    correlationId,
                    "updated-intent-id",
                    "updated-secret",
                    "completed"
            );
            when(eventPublisherService.sendUpdatePaymentIntent(basketId, BigDecimal.valueOf(27.97)))
                    .thenReturn(CompletableFuture.completedFuture(response));

            Basket result = orderService.createOrUpdatePaymentIntent(basketId, correlationId);

            assertEquals("updated-intent-id", result.getPaymentIntentId());
            assertEquals("updated-secret", result.getClientSecret());

            verify(basketRepository).save(testBasket);
        }

        @Test
        @DisplayName("Should throw BasketNotFoundException when basket not found")
        void shouldThrowBasketNotFoundExceptionWhenBasketNotFound() {
            when(basketRepository.findById(basketId)).thenReturn(Optional.empty());

            assertThrows(BasketNotFoundException.class,
                    () -> orderService.createOrUpdatePaymentIntent(basketId, correlationId));
        }

        @Test
        @DisplayName("Should throw InvalidProductException when basket contains invalid items")
        void shouldThrowInvalidProductExceptionWhenBasketContainsInvalidItems() {
            when(basketRepository.findById(basketId)).thenReturn(Optional.of(testBasket));

            when(productServiceClient.validateBasketItems(any(), any()))
                    .thenReturn(ResponseEntity.ok(List.of(
                            new ProductValidationResult(testBasket.getItems().get(0).getProductId(), false, "Invalid")
                    )));

            assertThrows(InvalidProductException.class,
                    () -> orderService.createOrUpdatePaymentIntent(basketId, correlationId));
        }

        @Test
        @DisplayName("Should throw PaymentProcessingException when payment intent creation fails")
        void shouldThrowPaymentProcessingExceptionWhenPaymentIntentCreationFails() {
            when(basketRepository.findById(basketId)).thenReturn(Optional.of(testBasket));

            when(productServiceClient.validateBasketItems(any(), any()))
                    .thenReturn(ResponseEntity.ok(List.of(
                            new ProductValidationResult(testBasket.getItems().get(0).getProductId(), true, "Valid")
                    )));

            when(deliveryMethodRepository.findById(TEST_DELIVERY_METHOD_ID))
                    .thenReturn(Optional.of(testDeliveryMethod));

            when(eventPublisherService.sendCreatePaymentIntent(any(), any()))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Payment failed")));

            assertThrows(PaymentProcessingException.class,
                    () -> orderService.createOrUpdatePaymentIntent(basketId, correlationId));
        }
    }

    @Nested
    @DisplayName("Create or Update Order")
    class CreateOrUpdateOrderTests {

        @Test
        @DisplayName("Should create new order successfully")
        void shouldCreateNewOrderSuccessfully() {

            testBasket.setPaymentIntentId(paymentIntentId);
            testOrder.setOwner(testOwner);
            testOwner.setOrders(List.of(testOrder));


            BasketItem basketItem = BasketItem.builder()
                    .productId(UUID.randomUUID())
                    .quantity(2)
                    .price(BigDecimal.valueOf(10.99))
                    .build();
            testBasket.setItems(List.of(basketItem));


            when(basketRepository.findById(basketId)).thenReturn(Optional.of(testBasket));


            List<BasketItemDTO> expectedDtos = List.of(
                    new BasketItemDTO(basketItem.getProductId(), basketItem.getQuantity(),basketItem.getPrice())
            );


            when(orderRepository.findByPaymentIntentId(paymentIntentId)).thenReturn(Optional.empty());
            when(deliveryMethodRepository.findById(TEST_DELIVERY_METHOD_ID))
                    .thenReturn(Optional.of(testDeliveryMethod));
            when(ownerService.findOwnerByEmail(userEmail)).thenReturn(testOwner);
            when(orderRepository.save(any())).thenReturn(testOrder);


            Order result = orderService.createOrUpdateOrder(testOrderDTO, correlationId);


            assertNotNull(result);

            verify(basketRepository).findById(basketId);
            verify(deliveryMethodRepository).findById(TEST_DELIVERY_METHOD_ID);
            verify(ownerService).findOwnerByEmail(userEmail);
            verify(orderRepository).save(any());
            verify(eventPublisherService).publishCompletedOrder(any());
        }

        @Test
        @DisplayName("Should throw IllegalStateException when order already exists")
        void shouldThrowIllegalStateExceptionWhenOrderAlreadyExists() {

            testBasket.setPaymentIntentId(paymentIntentId);
            when(basketRepository.findById(basketId)).thenReturn(Optional.of(testBasket));
            when(orderRepository.findByPaymentIntentId(paymentIntentId)).thenReturn(Optional.of(testOrder));


            assertThrows(IllegalStateException.class,
                    () -> orderService.createOrUpdateOrder(testOrderDTO, correlationId));
        }

        @Test
        @DisplayName("Should throw BasketNotFoundException when basket not found")
        void shouldThrowBasketNotFoundExceptionWhenBasketNotFound() {
            when(basketRepository.findById(basketId)).thenReturn(Optional.empty());

            assertThrows(BasketNotFoundException.class,
                    () -> orderService.createOrUpdateOrder(testOrderDTO, correlationId));
        }

        @Test
        @DisplayName("Should throw DeliveryMethodNotFoundException when delivery method not found")
        void shouldThrowDeliveryMethodNotFoundExceptionWhenDeliveryMethodNotFound() {

            testBasket.setPaymentIntentId(paymentIntentId);
            when(basketRepository.findById(basketId)).thenReturn(Optional.of(testBasket));
            when(orderRepository.findByPaymentIntentId(paymentIntentId)).thenReturn(Optional.empty());
            when(deliveryMethodRepository.findById(TEST_DELIVERY_METHOD_ID)).thenReturn(Optional.empty());



            assertThrows(DeliveryMethodNotFoundException.class,
                    () -> orderService.createOrUpdateOrder(testOrderDTO, correlationId));
        }
    }

    @Nested
    @DisplayName("Get All Orders For User")
    class GetAllOrdersForUserTests {

        @Test
        @DisplayName("Should return all orders for user")
        void shouldReturnAllOrdersForUser() {
            IOrderDto orderDto = mock(IOrderDto.class);
            when(ownerService.findOwnerByEmail(userEmail)).thenReturn(testOwner);
            when(orderMapper.mapOrderToIOrderDto(testOrder)).thenReturn(orderDto);

            List<IOrderDto> result = orderService.getAllOrdersForUser(userEmail);

            assertEquals(1, result.size());
            assertEquals(orderDto, result.get(0));
        }

        @Test
        @DisplayName("Should return empty list when user has no orders")
        void shouldReturnEmptyListWhenUserHasNoOrders() {
            testOwner.setOrders(new ArrayList<>());
            when(ownerService.findOwnerByEmail(userEmail)).thenReturn(testOwner);

            List<IOrderDto> result = orderService.getAllOrdersForUser(userEmail);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Order By Id")
    class GetOrderByIdTests {

        @Test
        @DisplayName("Should return order by id")
        void shouldReturnOrderById() {
            IOrderDto orderDto = mock(IOrderDto.class);
            when(orderRepository.getOrderByOrderId(orderId)).thenReturn(Optional.of(testOrder));
            when(orderMapper.mapOrderToIOrderDto(testOrder)).thenReturn(orderDto);

            IOrderDto result = orderService.getOrderById(orderId);

            assertEquals(orderDto, result);
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException when order not found")
        void shouldThrowOrderNotFoundExceptionWhenOrderNotFound() {
            when(orderRepository.getOrderByOrderId(orderId)).thenReturn(Optional.empty());

            assertThrows(OrderNotfoundException.class,
                    () -> orderService.getOrderById(orderId));
        }
    }

    @Nested
    @DisplayName("Update Order Status By Payment Intent Id")
    class UpdateOrderStatusByPaymentIntentIdTests {

        @Test
        @DisplayName("Should update order status successfully")
        void shouldUpdateOrderStatusSuccessfully() {
            when(orderRepository.findByPaymentIntentId(paymentIntentId)).thenReturn(Optional.of(testOrder));

            assertDoesNotThrow(() -> orderService.updateOrderStatusByPaymentIntentId(paymentIntentId, OrderStatus.PAYMENT_RECEIVED));

            verify(orderRepository).save(testOrder);
            assertEquals(OrderStatus.PAYMENT_RECEIVED, testOrder.getStatus());
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException when order not found")
        void shouldThrowOrderNotFoundExceptionWhenOrderNotFound() {
            when(orderRepository.findByPaymentIntentId(paymentIntentId)).thenReturn(Optional.empty());

            assertThrows(OrderNotfoundException.class,
                    () -> orderService.updateOrderStatusByPaymentIntentId(paymentIntentId, OrderStatus.PAYMENT_RECEIVED));
        }
    }
}