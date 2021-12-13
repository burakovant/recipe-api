package com.crediteuropebank.recipeapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
	public static final String RECIPES_API_URI = "/users/%s/recipes";

	public static final String NOTFOUND_ERROR_MESSAGE = "Recipe cannot be found.";
	public static final String USER_FORBIDDEN_MESSAGE = "User access is not authorized to the recipe resource";

	public static final String VEGETARIAN_NULL_ERR_MSG = "vegetarian field cannot be null.";
	public static final String SERVINGS_NULL_ERR_MSG = "servings field cannot be null.";
	public static final String SERVINGS_MIN_ERR_MSG = "servings field should be 1 at least";
	public static final String INSTRUCTION_EMPTY_ERR_MSG = "instruction field cannot be empty.";
	public static final String INGREDIENTS_EMPTY_ERR_MSG = "ingredients list cannot be empty.";
}
