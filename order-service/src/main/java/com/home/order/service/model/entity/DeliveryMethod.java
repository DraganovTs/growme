package com.home.order.service.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "delivery_methods", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"short_name"})
})

@Builder
public class DeliveryMethod {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_method_id")
    private Integer deliveryMethodId;
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "short_name", nullable = false)
    private String shortName;
    @NotNull
    @Column(name = "delivery_time", nullable = false)
    private String deliveryTime;
    @Lob
    @Column(name = "description")
    private String description;
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;


}
