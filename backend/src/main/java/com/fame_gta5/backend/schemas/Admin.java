package com.fame_gta5.backend.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tbl_admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    @JsonProperty("id_admin")
    private int idAdmin;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

//    @OneToMany(mappedBy = "user")
//    @JsonProperty("sessions")
//    private List<Session> sessions;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
}
