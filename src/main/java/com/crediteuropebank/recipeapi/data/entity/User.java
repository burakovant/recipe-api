package com.crediteuropebank.recipeapi.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {

    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private String username;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Recipe> recipes;

    public User(String username) {
        this.username = username;
    }
}
