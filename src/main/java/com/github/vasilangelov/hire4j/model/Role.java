package com.github.vasilangelov.hire4j.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    public static final String USER = "ROLE_USER";

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    public static String getNormalizedRoleName(String roleName) {
        return switch (roleName) {
            case USER -> "User";
            case ADMIN -> "Admin";
            case SUPER_ADMIN -> "Super Admin";
            default -> "Unknown Role";
        };
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private byte id;

    @Column(unique = true, nullable = false)
    private String name;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    public byte getId() {
        return this.id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
