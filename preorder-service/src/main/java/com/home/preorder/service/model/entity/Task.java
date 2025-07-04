package com.home.preorder.service.model.entity;

import com.home.preorder.service.model.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity must be at most 10000")
    private Integer quantity;
    @NotBlank(message = "Unit is required")
    @Size(max = 20, message = "Unit must be at most 20 characters")
    private String unit;
    @NotBlank(message = "Quality standard is required")
    @Size(max = 50, message = "Quality standard must be at most 50 characters")
    private String quality;
    @NotNull(message = "Harvest date is required")
    @Future(message = "Harvest date must be in the future")
    @Column(name = "harvest_date")
    private LocalDate harvestDate;
    @NotNull(message = "Delivery date is required")
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;
    @Column(name = "flexible_dates")
    private boolean flexibleDates;
    @NotBlank(message = "Delivery location is required")
    @Size(max = 100, message = "Delivery location must be at most 100 characters")
    @Column(name = "delivery_location")
    private String deliveryLocation;
    @NotBlank(message = "Delivery method is required")
    @Size(max = 50, message = "Delivery method must be at most 50 characters")
    @Column(name = "delivery_method")
    private String deliveryMethod;
    @Column(name = "willing_to_ship")
    private boolean willingToShip;
    @NotBlank(message = "Price model is required")
    @Size(max = 50, message = "Price model must be at most 50 characters")
    @Column(name = "price_model")
    private String priceModel;
    @Column(name = "photos_required")
    private boolean photosRequired;
    @Column(name = "visit_farm")
    private boolean visitFarm;

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
