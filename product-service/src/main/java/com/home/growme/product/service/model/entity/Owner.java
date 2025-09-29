package com.home.growme.product.service.model.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "owners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner {

    @Id
    @Column(name = "owner_id")
    private UUID ownerId;
    @NotBlank(message = "Owner name must not be blank")
    @Size(max = 30, message = "Owner name must be at most 30 characters")
    @Column(name = "owner_name")
    private String ownerName;
    @Email(message = "Owner email should be valid")
    @NotBlank(message = "Owner email must not be blank")
    @Size(max = 30, message = "Owner email must be at most 30 characters")
    @Column(name = "owner_email")
    private String ownerEmail;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    @PrePersist
    public void generateId() {
        if (ownerId == null) {
            ownerId = UUID.randomUUID();
        }
    }


}
