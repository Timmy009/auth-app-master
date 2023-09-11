package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String password;
    @Column(name = "email", unique=true)
    private String email;
    private String phoneNumber;
//    @Enumerated(EnumType.STRING)
    private LoginProvider loginProvider;


    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
   private Set<Role> userRoles;
    private boolean isValid = false;



}
