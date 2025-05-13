package com.home.order.service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.home.order.service.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;
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
    private double subTotal;
    private String paymentIntentId;

    public Order(String buyerEmail, Address shipToAddress, List<OrderItem> orderItems, DeliveryMethod deliveryMethod,
                 double subTotal, String paymentIntentId) {
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
        if (deliveryMethod == null) {
            return BigDecimal.valueOf(subTotal);
        }
        return BigDecimal.valueOf(subTotal).add(deliveryMethod.getPrice());
    }
}