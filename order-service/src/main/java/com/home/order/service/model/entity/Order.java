package com.home.order.service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.home.order.service.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @Column(name = "order_id")
    private UUID orderId;
    @Column(name = "buyer_email", nullable = false)
    private String buyerEmail;
    @Column(name = "order_date", nullable = false)
    private Instant orderDate;
    @Embedded
    @Column(name = "ship_to_address")
    private Address shipToAddress;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    @JoinColumn(name = "deliveryMethodId", nullable = false)
    private DeliveryMethod deliveryMethod;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;
    @Column(name = "sub_total", nullable = false)
    private BigDecimal subTotal;
    private String paymentIntentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    public Order(String buyerEmail, Address shipToAddress, List<OrderItem> orderItems, DeliveryMethod deliveryMethod,
                 BigDecimal subTotal, String paymentIntentId) {
        this.buyerEmail = buyerEmail;
        this.shipToAddress = shipToAddress;
        this.orderItems = orderItems;
        this.deliveryMethod = deliveryMethod;
        this.subTotal = subTotal;
        this.paymentIntentId = paymentIntentId;
        this.status = OrderStatus.PENDING;
        this.orderDate = Instant.now();
    }

    @JsonInclude
    @Transient
    public BigDecimal getTotal() {
        if (subTotal == null) {
            return BigDecimal.ZERO;
        }
        
        if (deliveryMethod == null) {
            return subTotal;
        }
        
        return subTotal.add(deliveryMethod.getPrice() != null ? deliveryMethod.getPrice() : BigDecimal.ZERO);
    }
}