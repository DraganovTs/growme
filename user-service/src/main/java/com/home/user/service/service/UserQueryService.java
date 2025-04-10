package com.home.user.service.service;

import com.home.user.service.model.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserQueryService {
    UserDTO getUserById(UUID userId);
    List<UserDTO>getUsersByRole(String role);
    List<UUID>getUsersOwnedProducts(String userId);
}
