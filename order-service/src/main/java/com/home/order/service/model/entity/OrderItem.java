package com.home.order.service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "order")
public class OrderItem {

    @Id
    @Column(name = "order_items_id")
    private UUID orderItemId;

    @Embedded
    private ProductItemOrdered itemOrdered;

    private int quantity ;
    private BigDecimal price;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "order_id" , referencedColumnName = "order_id")
    private Order order;


    public OrderItem(ProductItemOrdered itemOrdered , int quantity,  BigDecimal price) {
        this.price = price;
        this.quantity = quantity;
        this.itemOrdered = itemOrdered;
    }

    @PrePersist
    public void generateId() {
        if (orderItemId == null) {
            orderItemId = UUID.randomUUID();
        }
    }
}