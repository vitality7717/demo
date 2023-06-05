package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
//@Entity
@AllArgsConstructor
@NoArgsConstructor
//@Table(name = "users")
public class User {
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(nullable = false, unique = true)
    private String email;

    //@Column(nullable = false)
    private String name;

    //@Column(nullable = false)
    private String password;

    //@Enumerated(EnumType.STRING)
    //@Column(nullable = false)
    private Set<Role> role;
}
