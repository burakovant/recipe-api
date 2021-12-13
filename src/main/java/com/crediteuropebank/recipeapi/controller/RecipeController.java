package com.crediteuropebank.recipeapi.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crediteuropebank.recipeapi.model.dto.RecipeDTO;
import com.crediteuropebank.recipeapi.service.RecipeService;
import com.crediteuropebank.recipeapi.util.Constants;
import com.crediteuropebank.recipeapi.util.RecipeAPIException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("users")
@Slf4j
@Api(value = "Recipe Api documentation", authorizations = { @Authorization(value = "basicAuth") })
public class RecipeController {

	@Autowired
	private RecipeService service;

	@PostMapping("/{username}/recipes")
	@ApiOperation(value = "Create new recipe.", authorizations = { @Authorization(value = "basicAuth") })
	public RecipeDTO create(@PathVariable @ApiParam(value = "username", example = "userA") String username,
			@Valid @RequestBody @ApiParam(value = "New recipe resource.") RecipeDTO newRecipe)
			throws RecipeAPIException {
		try {
			validateAuthUsername(SecurityContextHolder.getContext().getAuthentication().getName(), username);
			return service.create(newRecipe, username);
		} catch (Exception e) {
			throw handleException("create", e);
		}
	}

	@GetMapping("/{username}/recipes/{id}")
	@ApiOperation(value = "Get recipe identified by unique Id in URL.", authorizations = {
			@Authorization(value = "basicAuth") })
	public RecipeDTO get(@PathVariable @ApiParam(value = "username", example = "userA") String username,
			@PathVariable @ApiParam(value = "Unique Id of recipe resource.", example = "1") Long id)
			throws RecipeAPIException {
		try {
			validateAuthUsername(SecurityContextHolder.getContext().getAuthentication().getName(), username);
			return service.get(username, id);
		} catch (Exception e) {
			throw handleException("get", e);
		}
	}

	@PutMapping("/{username}/recipes/{id}")
	@ApiOperation(value = "Update the recipe identified by unique Id in URL.", authorizations = {
			@Authorization(value = "basicAuth") })
	public RecipeDTO update(@PathVariable @ApiParam(value = "username", example = "userA") String username,
			@Valid @RequestBody @ApiParam(value = "Recipe to be updated") RecipeDTO recipe,
			@PathVariable @ApiParam(value = "Unique Id of recipe resource.", example = "1") Long id)
			throws RecipeAPIException {
		try {
			validateAuthUsername(SecurityContextHolder.getContext().getAuthentication().getName(), username);
			return service.update(recipe, username, id);
		} catch (Exception e) {
			throw handleException("update", e);
		}
	}

	@ApiOperation(value = "Delete recipe identified by unique Id in URL.", authorizations = {
			@Authorization(value = "basicAuth") })
	@DeleteMapping("/{username}/recipes/{id}")
	public void delete(@PathVariable @ApiParam(value = "username", example = "userA") String username,
			@PathVariable @ApiParam(value = "Unique Id of recipe resource.", example = "1") Long id)
			throws RecipeAPIException {
		try {
			validateAuthUsername(SecurityContextHolder.getContext().getAuthentication().getName(), username);
			service.delete(username, id);
		} catch (Exception e) {
			throw handleException("delete", e);
		}
	}

	@GetMapping("/{username}/recipes")
	@ApiOperation(value = "Get all recipes.", authorizations = { @Authorization(value = "basicAuth") })
	public List<RecipeDTO> getAll(@PathVariable @ApiParam(value = "username", example = "userA") String username)
			throws RecipeAPIException {
		try {
			validateAuthUsername(SecurityContextHolder.getContext().getAuthentication().getName(), username);
			return service.getAll(username);
		} catch (Exception e) {
			throw handleException("getAll", e);
		}
	}

	private void validateAuthUsername(String authUser, String username) throws RecipeAPIException {
		if (!authUser.equals(username)) {
			throw new RecipeAPIException(Constants.USER_FORBIDDEN_MESSAGE);
		}
	}

	private RecipeAPIException handleException(String methodName, Exception e) {
		log.error(methodName + " exception: ", e);
		return new RecipeAPIException(e.getMessage());
	}
}
