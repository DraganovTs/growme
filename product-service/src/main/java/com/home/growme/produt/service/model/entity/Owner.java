package com.home.growme.produt.service.model.entity;


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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "owner_id",columnDefinition = "CHAR(36)")
    private UUID ownerId;
    @Column(name = "owner_name")
    private String ownerName;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

}
