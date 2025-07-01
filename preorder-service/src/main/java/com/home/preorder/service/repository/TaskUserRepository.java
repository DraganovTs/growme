package com.home.preorder.service.repository;

import com.home.preorder.service.model.entity.TaskUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskUserRepository extends JpaRepository<TaskUser, UUID> {
}
