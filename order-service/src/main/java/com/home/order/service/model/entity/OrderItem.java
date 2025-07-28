package com.home.order.service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "ItemOrdered must not be null")
    @Valid
    private ProductItemOrdered itemOrdered;
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity ;
    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
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