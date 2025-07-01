package com.home.preorder.service.model.entity;

import com.home.preorder.service.model.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @Column(name = "task_id")
    private UUID taskId;

    @NotBlank(message = "Title must not be blank")
    @Size(max = 100, message = "Title must be at most 100 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 1000, message = "Description must be at most 1000 characters")
    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @NotNull(message = "User ID must be provided")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @DecimalMin(value = "0.0", message = "Budget must be positive or zero")
    @Digits(integer = 10, fraction = 2, message = "Invalid budget format")
    private BigDecimal budget;

    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (taskId == null) {
            taskId = UUID.randomUUID();
        }
        if (status == null) {
            status = TaskStatus.OPEN;
        }
    }


}
