package com.home.preorder.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "task_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUser {
    @Id
    @Column(name = "task_user_id")
    private UUID ownerId;
    @NotBlank(message = "User name must not be blank")
    @Size(max = 30, message = "User name must be at most 30 characters")
    @Column(name = "task_user_name")
    private String ownerName;
    @Email(message = "User email should be valid")
    @NotBlank(message = "User email must not be blank")
    @Size(max = 30, message = "User email must be at most 30 characters")
    @Column(name = "task_user_email")
    private String ownerEmail;
}
