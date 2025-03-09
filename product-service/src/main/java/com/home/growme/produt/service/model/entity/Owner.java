package com.home.growme.produt.service.model.entity;


import com.home.growme.produt.service.model.dto.OwnerDTO;
import jakarta.persistence.*;
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
    @Column(name = "owner_name")
    private String ownerName;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    @PrePersist
    public void generateId() {
        if (ownerId == null) {
            ownerId = UUID.randomUUID(); // Manually generate UUID
        }
    }

    public OwnerDTO mapOwnerToOwnerDTO(Owner owner) {
        return OwnerDTO.builder()
                .ownerId(owner.getOwnerId())
                .ownerName(owner.getOwnerName())
                .build();
    }
}
