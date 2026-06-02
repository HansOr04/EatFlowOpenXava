package com.grupo4.Eatflow.modelo;

import java.math.*;
import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Table(name = "product")
@Views({
    @View(members = "name, category; description; price, available, stock; imageUrl"),
    @View(name = "Simple", members = "name, price")
})
@Tab(properties = "name, category.name, price, available",
     defaultOrder = "available desc, name asc")
@Getter @Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 150, nullable = false)
    @Required
    String name;

    @Column(length = 500)
    String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @Required
    BigDecimal price;

    @Column(length = 500)
    String imageUrl;

    @Column(nullable = false)
    Boolean available = true;

    Integer stock;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @Required
    @ReferenceView("Simple")
    Category category;
}
