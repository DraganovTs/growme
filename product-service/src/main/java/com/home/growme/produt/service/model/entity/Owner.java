package com.home.growme.produt.service.model.entity;


import com.home.growme.common.module.valuobjects.OwnedId;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "owners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner {

    @EmbeddedId
    @Column(name = "owner_id")
    private OwnedId ownedId;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

}
