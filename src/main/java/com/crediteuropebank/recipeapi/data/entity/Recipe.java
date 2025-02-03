package com.crediteuropebank.recipeapi.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Recipe {

    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    private Boolean active;
    private LocalDateTime createDate;
    private Boolean vegetarian;
    private Integer servings;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ingredients")
    private List<String> ingredients;
    private String instruction;
}
