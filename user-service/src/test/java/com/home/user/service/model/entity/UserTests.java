package com.home.user.service.model.entity;

import com.home.user.service.model.enums.AccountStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
public class UserTests {
    private Validator validator;
    private User.UserBuilder userBuilder;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        userBuilder = User.builder()
                .userId(UUID.randomUUID())
                .username("validUser")
                .email("valid@example.com")
                .accountStatus(AccountStatus.PENDING);
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate valid user")
        void shouldValidateValidUser() {
            User user = userBuilder.build();
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail when username is blank")
        void shouldFailWhenUsernameIsBlank() {
            User user = userBuilder.username("").build();
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().contains("Username is required")));
        }

        @Test
        @DisplayName("Should fail when username is too short")
        void shouldFailWhenUsernameIsTooShort() {
            User user = userBuilder.username("abc").build();
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().contains("Username must be between 4 and 20 characters")));
        }

        @Test
        @DisplayName("Should fail when email is null")
        void shouldFailWhenEmailIsNull() {
            User user = userBuilder.email(null).build();
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail when email is invalid")
        void shouldFailWhenEmailIsInvalid() {
            User user = userBuilder.email("invalid-email").build();
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Invalid email format")));
        }
    }

    @Nested
    @DisplayName("Entity Behavior Tests")
    class EntityBehaviorTests {

        @Test
        @DisplayName("Should initialize timestamps on creation")
        void shouldInitializeTimestampsOnCreation() {
            User user = new User();
            assertNotNull(user.getCreatedAt());
            assertNotNull(user.getUpdatedAt());
        }

        @Test
        @DisplayName("Should update timestamp on change")
        void shouldUpdateTimestampOnChange() throws InterruptedException {
            User user = new User();
            Date initialUpdatedAt = user.getUpdatedAt();

            Thread.sleep(1);
            user.setUsername("newUsername");


            user.onUpdate();

            assertTrue(user.getUpdatedAt().after(initialUpdatedAt));
        }

        @Test
        @DisplayName("Should set default account status to PENDING")
        void shouldSetDefaultAccountStatus() {
            User user = new User();
            assertEquals(AccountStatus.PENDING, user.getAccountStatus());
        }
    }

    @Nested
    @DisplayName("Collections Management Tests")
    class CollectionsManagementTests {

        @Test
        @DisplayName("Should add role to user")
        void shouldAddRoleToUser() {
            User user = new User();
            user.getRoles().add("ADMIN");
            assertEquals(1, user.getRoles().size());
        }

        @Test
        @DisplayName("Should add product to owned products")
        void shouldAddProductToOwnedProducts() {
            User user = userBuilder.build();
            UUID productId = UUID.randomUUID();
            user.getOwnedProductIds().add(productId);

            assertEquals(1, user.getOwnedProductIds().size());
            assertTrue(user.getOwnedProductIds().contains(productId));
        }

        @Test
        @DisplayName("Should add order to purchased orders")
        void shouldAddOrderToPurchasedOrders() {
            User user = userBuilder.build();
            UUID orderId = UUID.randomUUID();
            user.getPurchasedOrderIds().add(orderId);

            assertEquals(1, user.getPurchasedOrderIds().size());
            assertTrue(user.getPurchasedOrderIds().contains(orderId));
        }


        @Test
        @DisplayName("Should initialize empty collections")
        void shouldInitializeEmptyCollections() {
            User user = new User();
            assertNotNull(user.getRoles());
            assertNotNull(user.getOwnedProductIds());
            assertNotNull(user.getPurchasedOrderIds());
        }
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should build user with all fields")
        void shouldBuildUserWithAllFields() {
            UUID userId = UUID.randomUUID();
            Date now = new Date();
            Address address = new Address();

            User user = userBuilder
                    .userId(userId)
                    .username("testUser")
                    .email("test@example.com")
                    .firstName("John")
                    .lastName("Doe")
                    .phone("+1234567890")
                    .address(address)
                    .accountStatus(AccountStatus.ACTIVE)
                    .roles(List.of("USER", "ADMIN"))
                    .ownedProductIds(List.of(UUID.randomUUID()))
                    .purchasedOrderIds(List.of(UUID.randomUUID()))
                    .build();

            assertEquals(userId, user.getUserId());
            assertEquals("testUser", user.getUsername());
            assertEquals("test@example.com", user.getEmail());
            assertEquals("John", user.getFirstName());
            assertEquals("Doe", user.getLastName());
            assertEquals("+1234567890", user.getPhone());
            assertEquals(address, user.getAddress());
            assertEquals(AccountStatus.ACTIVE, user.getAccountStatus());
            assertEquals(2, user.getRoles().size());
            assertEquals(1, user.getOwnedProductIds().size());
            assertEquals(1, user.getPurchasedOrderIds().size());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when userIds are equal")
        void shouldBeEqualWhenUserIdsAreEqual() {
            UUID userId = UUID.randomUUID();
            User user1 = User.builder().userId(userId).build();
            User user2 = User.builder().userId(userId).build();

            assertEquals(user1, user2);
            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when userIds are different")
        void shouldNotBeEqualWhenUserIdsAreDifferent() {
            User user1 = userBuilder.userId(UUID.randomUUID()).build();
            User user2 = userBuilder.userId(UUID.randomUUID()).build();

            assertNotEquals(user1, user2);
        }
    }
}

