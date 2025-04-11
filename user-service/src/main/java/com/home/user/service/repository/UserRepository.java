package com.home.user.service.repository;

import com.home.user.service.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Collection<User> findByRolesContaining(String role);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT u.email FROM User u WHERE u.userId = :userId")
    Optional<String> findEmailById(@Param("userId") UUID userId);}
