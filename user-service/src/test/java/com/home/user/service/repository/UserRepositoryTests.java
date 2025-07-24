package com.home.user.service.repository;

import com.home.user.service.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("User Repository Tests")
@EntityScan("com.home.user.service.model.entity")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private UUID userId;


    @BeforeEach
    void setUp() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .roles(List.of("Buyer", "Seller"))
                .build();

        userId = user.getUserId();
        entityManager.persistAndFlush(user);
    }

    @Test
    void shouldFindUsersByRole() {
        Collection<User> result = userRepository.findByRolesContaining("Seller");
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(u -> u.getUsername().equals("testuser")));
    }

    @Test
    void shouldReturnTrueIfEmailExists() {
        assertTrue(userRepository.existsByEmail("test@example.com"));
    }

    @Test
    void shouldReturnFalseIfEmailDoesNotExist() {
        assertFalse(userRepository.existsByEmail("notfound@example.com"));
    }

    @Test
    void shouldReturnTrueIfUsernameExists() {
        assertTrue(userRepository.existsByUsername("testuser"));
    }

    @Test
    void shouldReturnFalseIfUsernameDoesNotExist() {
        assertFalse(userRepository.existsByUsername("unknownuser"));
    }

    @Test
    void shouldReturnEmailById() {
        Optional<String> email = userRepository.findEmailById(userId);
        assertTrue(email.isPresent());
        assertEquals("test@example.com", email.get());
    }

    @Test
    void shouldReturnEmptyOptionalIfUserNotFound() {
        Optional<String> email = userRepository.findEmailById(UUID.randomUUID());
        assertTrue(email.isEmpty());
    }
}
