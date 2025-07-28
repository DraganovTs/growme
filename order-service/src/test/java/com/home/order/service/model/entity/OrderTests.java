package com.home.order.service.model.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Entity Tests")
public class OrderTests {
    private Validator validator;
    private DeliveryMethod deliveryMethod;
    private Address address;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        deliveryMethod = new DeliveryMethod("DHL","today","fast delivery",new BigDecimal(5));


        address = new Address("John","Doe","123 Main St","Anytown","NY","1000");

    }

    @Test
    @DisplayName("Should create valid Order")
    void shouldCreateValidOrder() {
        Order order = new Order(
                "buyer@example.com",
                address,
                Collections.emptyList(),
                deliveryMethod,
                BigDecimal.valueOf(99.99),
                "payment123"
        );

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail with null buyer email")
    void shouldFailWithNullBuyerEmail() {
        Order order = new Order(
                null,
                address,
                Collections.emptyList(),
                deliveryMethod,
                BigDecimal.TEN,
                "payment123"
        );

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("buyerEmail")));
    }

    @Test
    @DisplayName("Should fail with long buyer email")
    void shouldFailWithLongBuyerEmail() {
        String longEmail = "a".repeat(31) + "@example.com";

        Order order = new Order(
                longEmail,
                address,
                Collections.emptyList(),
                deliveryMethod,
                BigDecimal.TEN,
                "payment123"
        );

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("buyerEmail")));
    }

    @Test
    @DisplayName("Should allow null paymentIntentId")
    void shouldAllowNullPaymentIntentId() {
        Order order = new Order(
                "buyer@example.com",
                address,
                Collections.emptyList(),
                deliveryMethod,
                BigDecimal.valueOf(50),
                null
        );

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail with long paymentIntentId")
    void shouldFailWithLongPaymentIntentId() {
        String longId = "x".repeat(31);
        Order order = new Order(
                "buyer@example.com",
                address,
                Collections.emptyList(),
                deliveryMethod,
                BigDecimal.TEN,
                longId
        );

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("paymentIntentId")));
    }

    @Test
    @DisplayName("Should generate ID on persist if null")
    void shouldGenerateIdOnPersistIfNull() {
        Order order = new Order(
                "buyer@example.com",
                address,
                Collections.emptyList(),
                deliveryMethod,
                BigDecimal.TEN,
                "payment123"
        );
        order.setOrderId(null);

        order.generateId();

        assertNotNull(order.getOrderId());
    }

    @Test
    @DisplayName("Should keep existing ID on persist")
    void shouldKeepExistingIdOnPersist() {
        UUID existingId = UUID.randomUUID();
        Order order = new Order(
                "buyer@example.com",
                address,
                Collections.emptyList(),
                deliveryMethod,
                BigDecimal.TEN,
                "payment123"
        );
        order.setOrderId(existingId);

        order.generateId();

        assertEquals(existingId, order.getOrderId());
    }

    @Test
    @DisplayName("Should calculate total with delivery price")
    void shouldCalculateTotalWithDeliveryPrice() {
        Order order = new Order(
                "buyer@example.com",
                address,
                Collections.emptyList(),
                deliveryMethod,
                BigDecimal.valueOf(100),
                "payment123"
        );

        BigDecimal total = order.getTotal();
        assertEquals(BigDecimal.valueOf(105), total);
    }

    @Test
    @DisplayName("Should calculate total as subTotal when deliveryMethod is null")
    void shouldCalculateTotalWithoutDeliveryPrice() {
        Order order = new Order(
                "buyer@example.com",
                address,
                Collections.emptyList(),
                null,
                BigDecimal.valueOf(80),
                "payment123"
        );

        BigDecimal total = order.getTotal();
        assertEquals(BigDecimal.valueOf(80), total);
    }

    @Test
    @DisplayName("Should return zero when subTotal is null")
    void shouldReturnZeroWhenSubTotalIsNull() {
        Order order = new Order(
                "buyer@example.com",
                address,
                Collections.emptyList(),
                deliveryMethod,
                null,
                "payment123"
        );

        BigDecimal total = order.getTotal();
        assertEquals(BigDecimal.ZERO, total);
    }
}
