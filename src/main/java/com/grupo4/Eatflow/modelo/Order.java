package com.grupo4.Eatflow.modelo;

import java.math.*;
import java.time.*;
import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;
import org.openxava.jpa.XPersistence;
import com.grupo4.Eatflow.modelo.enums.OrderStatus;
import com.grupo4.Eatflow.util.OrderDiscountCalculator;
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
    @ReadOnly
    @Setter(AccessLevel.NONE)
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

    @Action("closeOrder")
    public void closeOrder() throws Exception {
        int totalItems = getItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
        double subtotal = getItems().stream()
                .map(OrderItem::getSubtotal)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
        double finalAmount = OrderDiscountCalculator.calculateFinalAmount(totalItems, subtotal);
        this.totalAmount = BigDecimal.valueOf(finalAmount);
        this.status = OrderStatus.SERVED;
        XPersistence.getManager().merge(this);
    }

    public void setStatus(OrderStatus nuevoEstado) {
        if (this.status != null && !esTransicionValida(this.status, nuevoEstado)) {
            throw new IllegalStateException(
                    "Transición de estado inválida: no se puede pasar de " + this.status + " a " + nuevoEstado);
        }
        this.status = nuevoEstado;
    }

    private boolean esTransicionValida(OrderStatus actual, OrderStatus nuevoEstado) {
        switch (actual) {
            case PENDING:
                return nuevoEstado == OrderStatus.PENDING || nuevoEstado == OrderStatus.IN_PROGRESS
                        || nuevoEstado == OrderStatus.SERVED || nuevoEstado == OrderStatus.CANCELLED;
            case IN_PROGRESS:
                return nuevoEstado == OrderStatus.IN_PROGRESS || nuevoEstado == OrderStatus.SERVED
                        || nuevoEstado == OrderStatus.CANCELLED;
            case SERVED:
            case CANCELLED:
            default:
                return false;
        }
    }

    @PrePersist
    void prePersist() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
    }
}
