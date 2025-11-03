package com.codingShuttle.projects.AirbnbApp.entity;

import com.codingShuttle.projects.AirbnbApp.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true , nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; //encode it


    private String name;


    @ElementCollection(fetch = FetchType.EAGER) //create another table for roles
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;



}
