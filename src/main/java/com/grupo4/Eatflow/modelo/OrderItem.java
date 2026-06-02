package com.grupo4.Eatflow.modelo;

import java.math.*;
import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Table(name = "order_item")
@View(members = "product, quantity, unitPrice, subtotal, specialInstructions")
@Getter @Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    @Required
    Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    @Required
    BigDecimal unitPrice;

    @Column(length = 255)
    String specialInstructions;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @Required
    Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @Required
    @ReferenceView("Simple")
    Product product;

    public BigDecimal getSubtotal() {
        if (quantity == null || unitPrice == null) return BigDecimal.ZERO;
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
