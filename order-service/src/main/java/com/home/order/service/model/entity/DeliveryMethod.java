package com.home.order.service.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

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
    @NotNull(message = "Short name is required")
    @Size(min = 1, max = 30, message = "Short name must be between 1 and 30 characters")
    @Column(name = "short_name", nullable = false, length = 30)
    private String shortName;
    @NotNull(message = "Delivery time is required")
    @Size(min = 1, max = 30, message = "Delivery time must be between 1 and 30 characters")
    @Column(name = "delivery_time", nullable = false, length = 30)
    private String deliveryTime;
    @Lob
    @Column(name = "description")
    private String description;
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "deliveryMethod", fetch = FetchType.LAZY)
    private Set<Order> orders;

    public DeliveryMethod(String shortName, String deliveryTime, String description, BigDecimal price) {
        this.shortName = shortName;
        this.deliveryTime = deliveryTime;
        this.description = description;
        this.price = price;
    }


}
