package com.grupo4.Eatflow.modelo;

import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Table(name = "restaurant_table")
@Views({
    @View(members = "tableNumber, capacity, active; qrCode"),
    @View(name = "Simple", members = "tableNumber")
})
@Tab(properties = "tableNumber, capacity, active",
     defaultOrder = "active desc, tableNumber asc")
@Getter @Setter
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    @Required
    Integer tableNumber;

    @Column(nullable = false, unique = true, length = 255)
    @Required
    String qrCode;

    Integer capacity;

    @Column(nullable = false)
    Boolean active = true;

    @OneToMany(mappedBy = "table")
    List<Order> orders = new ArrayList<>();
}
