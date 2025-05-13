package com.home.order.service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_items_id")
    private Integer orderItemId;

    @Embedded
    private ProductItemOrdered itemOrdered;

    private int quantity ;
    private double price;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "order_id" , referencedColumnName = "order_id")
    private Order order;


    public OrderItem(ProductItemOrdered itemOrdered , int quantity,  double price) {
        this.price = price;
        this.quantity = quantity;
        this.itemOrdered = itemOrdered;
    }
}