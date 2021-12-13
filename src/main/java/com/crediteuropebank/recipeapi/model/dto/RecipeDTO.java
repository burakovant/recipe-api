package com.crediteuropebank.recipeapi.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.crediteuropebank.recipeapi.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = "Recipe Api model documentation ")
public class RecipeDTO {

	@JsonProperty(access = Access.READ_ONLY)
	@ApiModelProperty(value = "Unique id field of recipe object", example = "1")
	private Long id;

	@JsonProperty(access = Access.READ_ONLY)
	@ApiModelProperty(value = "Active status of the recipe. 0 means deleted, 1 means active", example = "true")
	private Boolean active;

	@JsonProperty(access = Access.READ_ONLY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	@ApiModelProperty(value = "Create date of the recipe object", example = "09-12-2021 12:00")
	private LocalDateTime createDate;

	@NotNull(message = Constants.VEGETARIAN_NULL_ERR_MSG)
	@ApiModelProperty(value = "Specify whether the recipe is vegetarian or not."
			+ " 1 means vegetarian, 0 means not vegetarian", example = "false", required = true, position = 1)
	private Boolean vegetarian;

	@NotNull(message = Constants.SERVINGS_NULL_ERR_MSG)
	@Min(value = 1, message = Constants.SERVINGS_MIN_ERR_MSG)
	@ApiModelProperty(value = "Specify the number of people the recipe is suitable for. Minimum 1.", example = "4", required = true, position = 2)
	private Integer servings;

	@NotEmpty(message = Constants.INGREDIENTS_EMPTY_ERR_MSG)
	@ApiModelProperty(value = "List of ingredients", example = "[\"chicken\", \"salt\", \"pepper\"]", required = true, position = 3)
	private List<String> ingredients;

	@NotEmpty(message = Constants.INSTRUCTION_EMPTY_ERR_MSG)
	@ApiModelProperty(value = "Instructions of the recipe", example = "Fry chicken with salt and pepper", required = true, position = 4)
	private String instruction;

	public RecipeDTO(Boolean vegetarian, Integer servings, List<String> ingredients, String instruction) {
		this.vegetarian = vegetarian;
		this.servings = servings;
		this.ingredients = ingredients;
		this.instruction = instruction;
	}

}
