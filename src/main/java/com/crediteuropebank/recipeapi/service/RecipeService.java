package com.crediteuropebank.recipeapi.service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import com.crediteuropebank.recipeapi.data.entity.Recipe;
import com.crediteuropebank.recipeapi.data.entity.User;
import com.crediteuropebank.recipeapi.data.repository.RecipeRepository;
import com.crediteuropebank.recipeapi.data.repository.UserRepository;
import com.crediteuropebank.recipeapi.model.dto.RecipeDTO;
import com.crediteuropebank.recipeapi.util.Constants;
import com.crediteuropebank.recipeapi.util.RecipeAPIException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipeService {
	private final UserRepository userRepo;
	private final RecipeRepository recipeRepo;

	private static final ModelMapper modelMapper = new ModelMapper();

	public RecipeDTO create(RecipeDTO newRecipe, String username) throws RecipeAPIException {
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new RecipeAPIException(Constants.USER_FORBIDDEN_MESSAGE));

		Recipe recipe = modelMapper.map(newRecipe, Recipe.class);
		recipe.setUser(user);
		recipe.setActive(true);
		recipe.setCreateDate(LocalDateTime.now());

		return modelMapper.map(recipeRepo.save(recipe), RecipeDTO.class);
	}

	public RecipeDTO update(RecipeDTO recipeDTO, String username, Long recipeId) throws RecipeAPIException {
		if (!userRepo.existsByUsername(username))
			throw new RecipeAPIException(Constants.USER_FORBIDDEN_MESSAGE);

		Recipe recipe = recipeRepo.findById(recipeId)
				.orElseThrow(() -> new RecipeAPIException(Constants.NOTFOUND_ERROR_MESSAGE));

		if (!username.equals(recipe.getUser().getUsername())) {
			throw new RecipeAPIException(Constants.USER_FORBIDDEN_MESSAGE);
		}
		// Do not use modelMapper. API client may have changed the create time and
		// active status unwillingly, so to be safe, transfer data from DTO to Entity
		// manually.
		recipe.setVegetarian(recipeDTO.getVegetarian());
		recipe.setServings(recipeDTO.getServings());
		recipe.setIngredients(recipeDTO.getIngredients());
		recipe.setInstruction(recipeDTO.getInstruction());
		return modelMapper.map(recipeRepo.save(recipe), RecipeDTO.class);
	}

	@Transactional
	public void delete(String username, Long recipeId) throws RecipeAPIException {
		if (!recipeRepo.existsById(recipeId))
			throw new RecipeAPIException(Constants.NOTFOUND_ERROR_MESSAGE);

		recipeRepo.deleteRecipe(false, recipeId);
		log.info("Recipe with id " + recipeId + " was deleted.");
	}

	public RecipeDTO get(String username, Long recipeId) throws RecipeAPIException {
		if (!userRepo.existsByUsername(username))
			throw new RecipeAPIException(Constants.USER_FORBIDDEN_MESSAGE);

		Recipe recipe = recipeRepo.findById(recipeId)
				.orElseThrow(() -> new RecipeAPIException(Constants.NOTFOUND_ERROR_MESSAGE));

		// if record is found but still logically deleted, throw not found again.
		if (!recipe.getActive().booleanValue()) {
			throw new RecipeAPIException(Constants.NOTFOUND_ERROR_MESSAGE);
		}

		return modelMapper.map(recipe, RecipeDTO.class);
	}

	public List<RecipeDTO> getAll(String username) throws RecipeAPIException {
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new RecipeAPIException(Constants.USER_FORBIDDEN_MESSAGE));

		Type listType = new TypeToken<List<RecipeDTO>>() {
		}.getType();
		return modelMapper.map(recipeRepo.findAllByUserOrderByIdAsc(user), listType);
	}
}
