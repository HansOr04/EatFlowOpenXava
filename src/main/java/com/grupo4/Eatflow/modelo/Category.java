package com.grupo4.Eatflow.modelo;

import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Table(name = "category")
@Views({
    @View(members = "name, description, active"),
    @View(name = "Simple", members = "name")
})
@Tab(properties = "name, description, active",
     defaultOrder = "active desc, name asc")
@Getter @Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 100, nullable = false)
    @Required
    String name;

    @Column(length = 255)
    String description;

    @Column(nullable = false)
    Boolean active = true;

    @OneToMany(mappedBy = "category")
    List<Product> products = new ArrayList<>();
}
