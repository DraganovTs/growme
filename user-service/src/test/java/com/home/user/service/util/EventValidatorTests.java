package com.home.user.service.util;

import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.ProductAssignedToUserEvent;
import com.home.growme.common.module.events.ProductDeletionToUserEvent;
import com.home.growme.common.module.events.RoleAssignmentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Event Validator Tests")
public class EventValidatorTests {
    private EventValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EventValidator();
    }

    @Nested
    @DisplayName("validateRoleAssignmentResult")
    class RoleAssignmentResultValidation {
        @Test
        void shouldNotThrowForValidInput() {
            RoleAssignmentResult result = new RoleAssignmentResult(UUID.randomUUID().toString(), "SELLER",true);
            assertDoesNotThrow(() -> validator.validateRoleAssignmentResult(result));
        }

        @Test
        void shouldThrowForNullResult() {
            assertThrows(IllegalArgumentException.class, () -> validator.validateRoleAssignmentResult(null));
        }

        @Test
        void shouldThrowForEmptyUserId() {
            RoleAssignmentResult result = new RoleAssignmentResult("", "",true);
            assertThrows(IllegalArgumentException.class, () -> validator.validateRoleAssignmentResult(result));
        }
    }

    @Nested
    @DisplayName("validateProductAssignment")
    class ProductAssignmentValidation {
        @Test
        void shouldNotThrowForValidEvent() {
            ProductAssignedToUserEvent event = new ProductAssignedToUserEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            assertDoesNotThrow(() -> validator.validateProductAssignment(event));
        }

        @Test
        void shouldThrowForNullEvent() {
            assertThrows(IllegalArgumentException.class, () -> validator.validateProductAssignment(null));
        }

        @Test
        void shouldThrowForEmptyUserId() {
            ProductAssignedToUserEvent event = new ProductAssignedToUserEvent("", UUID.randomUUID().toString());
            assertThrows(IllegalArgumentException.class, () -> validator.validateProductAssignment(event));
        }

        @Test
        void shouldThrowForEmptyProductId() {
            ProductAssignedToUserEvent event = new ProductAssignedToUserEvent(UUID.randomUUID().toString(), "");
            assertThrows(IllegalArgumentException.class, () -> validator.validateProductAssignment(event));
        }
    }

    @Nested
    @DisplayName("validateProductDeletion")
    class ProductDeletionValidation {
        @Test
        void shouldNotThrowForValidEvent() {
            ProductDeletionToUserEvent event = new ProductDeletionToUserEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            assertDoesNotThrow(() -> validator.validateProductDeletion(event));
        }

        @Test
        void shouldThrowForNullEvent() {
            assertThrows(IllegalArgumentException.class, () -> validator.validateProductDeletion(null));
        }

        @Test
        void shouldThrowForEmptyUserId() {
            ProductDeletionToUserEvent event = new ProductDeletionToUserEvent("", UUID.randomUUID().toString());
            assertThrows(IllegalArgumentException.class, () -> validator.validateProductDeletion(event));
        }

        @Test
        void shouldThrowForEmptyProductId() {
            ProductDeletionToUserEvent event = new ProductDeletionToUserEvent(UUID.randomUUID().toString(), "");
            assertThrows(IllegalArgumentException.class, () -> validator.validateProductDeletion(event));
        }
    }

    @Nested
    @DisplayName("validateOrderCompleted")
    class OrderCompletedValidation {
        @Test
        void shouldNotThrowForValidEvent() {
            OrderCompletedEvent event = new OrderCompletedEvent(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    "buyer@example.com",
                    Collections.emptyList(),
                    new BigDecimal("50.00"),
                    Instant.now()
            );
            assertDoesNotThrow(() -> validator.validateOrderCompleted(event));
        }

        @Test
        void shouldThrowForNullEvent() {
            assertThrows(IllegalArgumentException.class, () -> validator.validateOrderCompleted(null));
        }

        @Test
        void shouldThrowForEmptyUserId() {
            OrderCompletedEvent event = new OrderCompletedEvent(
                    UUID.randomUUID().toString(),
                    "",
                    "buyer@example.com",
                    Collections.emptyList(),
                    new BigDecimal("50.00"),
                    Instant.now()
            );
            assertThrows(IllegalArgumentException.class, () -> validator.validateOrderCompleted(event));
        }

        @Test
        void shouldThrowForNullOrderId() {
            OrderCompletedEvent event = new OrderCompletedEvent(
                    null,
                    UUID.randomUUID().toString(),
                    "buyer@example.com",
                    Collections.emptyList(),
                    new BigDecimal("50.00"),
                    Instant.now()
            );
            assertThrows(IllegalArgumentException.class, () -> validator.validateOrderCompleted(event));
        }
    }
}
