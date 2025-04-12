package com.home.user.service.service;

import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserQueryService {
    UserDTO getUserById(UUID userId);
    User getUserEntityById(UUID userId);
    String getUserEmail(UUID userId);
    List<UserDTO>getUsersByRole(String role);
    List<UUID>getUsersOwnedProducts(String userId);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
