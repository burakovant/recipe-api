package com.crediteuropebank.recipeapi.data.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Recipe {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
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
