package com.home.user.service.repository;

import com.home.user.service.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Collection<User> findByRolesContaining(String role);
}
