package com.grupo4.Eatflow.modelo;

import java.time.*;
import javax.persistence.*;
import javax.validation.constraints.Email;
import org.openxava.annotations.*;
import com.grupo4.Eatflow.modelo.enums.AdminRole;
import lombok.*;

@Entity
@Table(name = "admin_user")
@View(members = "username, fullName; email, role; active, lastLogin; passwordHash")
@Tab(properties = "username, fullName, email, role, active",
     defaultOrder = "active desc, username asc")
@Getter @Setter
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 50, nullable = false, unique = true)
    @Required
    String username;

    @Email
    @Column(length = 150, nullable = false, unique = true)
    @Required
    String email;

    @Column(length = 255, nullable = false)
    @Required
    @Hidden
    String passwordHash;

    @Column(length = 150, nullable = false)
    @Required
    String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Required
    AdminRole role;

    @Column(nullable = false)
    Boolean active = true;

    LocalDateTime lastLogin;
}
