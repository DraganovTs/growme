package com.home.user.service.model.entity;

import com.home.user.service.model.enums.AccountStatus;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {

    @Id
    private UUID userId;

    @Column(nullable = false, unique = true, columnDefinition = "UUID")
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "user_owned_roles", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> roles = new ArrayList<>();


    @Column(nullable = true)
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Column(nullable = true)
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @Column(nullable = true, unique = true)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phone;

    @Embedded
    @Valid
    private Address address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.PENDING;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "user_owned_products", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "product_id")
    private List<UUID> ownedProductIds = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "user_purchased_orders", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "order_id")
    private List<UUID> purchasedOrderIds = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
