package com.home.order.service.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    @Size(max = 30, message = "Owner name must not exceed 30 characters")
    @Column(name = "owner_name")
    private String ownerName;
    @Size(max = 30, message = "Owner email must not exceed 30 characters")
    @Column(name = "owner_email")
    private String ownerEmail;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Order> orders;
}
