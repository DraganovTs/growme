package com.home.user.service.service;

import com.home.growme.common.module.dto.UserInfo;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * The UserQueryService interface provides a set of methods for querying user-related information
 * and performing lookups within the system. It operates as a read-only service, supplying details
 * about users and their associated data.
 */
public interface UserQueryService {
    UserDTO getUserById(UUID userId);
    User getUserEntityById(UUID userId);
    String getUserEmail(UUID userId);
    List<UserDTO>getUsersByRole(String role);
    List<UUID>getUsersOwnedProducts(String userId);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    UserInfo getUserInformation(String userId);
}
