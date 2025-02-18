package com.crediteuropebank.recipeapi.data.repository;

import com.crediteuropebank.recipeapi.data.entity.Recipe;
import com.crediteuropebank.recipeapi.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Modifying
    @Query("update Recipe r set r.active = ?1 where r.id = ?2")
    int deleteRecipe(Boolean status, Long id);

    List<Recipe> findAllByUserOrderByIdAsc(User user);
}
