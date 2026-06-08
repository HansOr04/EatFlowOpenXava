package com.grupo4.Eatflow.modelo;

import java.math.*;
import java.time.*;
import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;
import com.grupo4.Eatflow.modelo.enums.OrderStatus;
import lombok.*;

@Entity
@Table(name = "orders")
@View(members = "table, orderDate, status, notes; items")
@Tab(properties = "table.tableNumber, orderDate, status, totalAmount",
     defaultOrder = "orderDate desc")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, updatable = false)
    LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Required
    OrderStatus status = OrderStatus.PENDING;

    @Column(precision = 10, scale = 2)
    BigDecimal totalAmount;

    @Column(length = 500)
    String notes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant_table_id", nullable = false)
    @Required
    @ReferenceView("Simple")
    RestaurantTable table;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderItem> items = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
    }
}
