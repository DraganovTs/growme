package com.home.order.service.model.entity;


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
    @Column(name = "owner_email")
    private String ownerEmail;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;
}
