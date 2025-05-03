package com.home.order.service.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "delivery_method")
public class DeliveryMethod {


    //TODO add id and the end
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_method")
    private Integer deliveryMethodId;
    @Column(name = "short_name" , nullable = false)
    private String shortName;
    @Column(name = "delivery_time",nullable = false)
    private String deliveryTime;
    private String description;
    @Column(name = "price" , nullable = false)
    private BigDecimal price;

}
