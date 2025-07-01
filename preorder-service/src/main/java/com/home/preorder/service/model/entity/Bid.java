package com.home.preorder.service.model.entity;

import com.home.preorder.service.model.enums.BidStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bid {

    @Id
    @Column(name = "bid_id")
    private UUID bidId;

    @NotNull(message = "Price must be provided")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    @Digits(integer = 10, fraction = 2, message = "Price must have max 10 digits with 2 decimal places")
    @Column(nullable = false)
    private BigDecimal price;

    @NotBlank(message = "Message must not be blank")
    @Size(max = 500, message = "Message must be at most 500 characters")
    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus status;

    @NotNull(message = "User ID must be provided")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (bidId == null) {
            bidId = UUID.randomUUID();
        }
        if (status == null) {
            status = BidStatus.PENDING;
        }
    }
}
