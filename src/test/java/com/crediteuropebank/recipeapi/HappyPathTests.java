package com.crediteuropebank.recipeapi;

import com.crediteuropebank.recipeapi.model.dto.RecipeDTO;
import com.crediteuropebank.recipeapi.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Base64Utils;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class HappyPathTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    @DisplayName("Scenario 7: User creates a recipe")
    void saveFirstRecipe() throws Exception {
        String username = "userA";
        String password = "passA";

        RecipeDTO recipeDTO = new RecipeDTO(false, 4, Arrays.asList("chicken", "salt", "pepper"),
                "Fry chicken with salt and pepper");

        saveRecipeAndCheckFields(username, password, recipeDTO);
    }

    @Test
    @Order(2)
    @DisplayName("Scenario 8: User creates and updates a recipe")
    void saveSecondRecipeAndUpdate() throws Exception {
        String username = "userA";
        String password = "passA";

        RecipeDTO recipeDTO = new RecipeDTO(false, 6, Arrays.asList("chicken", "salt", "pepper"),
                "Fry chicken with salt and pepper");
        Long recipeId = saveRecipeAndCheckFields(username, password, recipeDTO);

        // update
        recipeDTO.setInstruction("Bake chicken with salt and pepper");
        recipeDTO.setIngredients(Arrays.asList("broccoli", "salt", "pepper"));
        recipeDTO.setVegetarian(true);
        recipeDTO.setServings(5);
        Long changedRecipeId = updateRecipeAndCheckFields(username, password, recipeDTO, recipeId);

        assertEquals(recipeId, changedRecipeId);
    }

    @Test
    @Order(3)
    @DisplayName("Scenario 9: User creates and deletes a recipe")
    void saveThirdRecipeAndDelete() throws Exception {
        String username = "userA";
        String password = "passA";
        String authHeaderStr = "Basic " + Base64Utils.encodeToString((username + ":" + password).getBytes());

        List<String> ingredients = Arrays.asList("chicken", "salt", "pepper");
        RecipeDTO recipeDTO = new RecipeDTO(false, 6, ingredients, "Fry chicken with salt and pepper");
        Long id = saveRecipeAndCheckFields(username, password, recipeDTO);

        // delete
        mockMvc.perform(delete(String.format(Constants.RECIPES_API_URI, username) + "/" + id).header("Authorization",
                authHeaderStr)).andExpect(status().isOk());

        // check the recipe resource actually logically deleted.
        mockMvc.perform(get(String.format(Constants.RECIPES_API_URI, username) + "/" + id).header("Authorization",
                authHeaderStr)).andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @DisplayName("Scenario 10: User tries to update another user's recipe")
    void forbidUpdateFromAnotherUser() throws Exception {
        String username = "userA";
        String password = "passA";

        RecipeDTO recipeDTO = new RecipeDTO(false, 4, Arrays.asList("chicken", "salt", "pepper"), "Test instruction3");
        Long recipeId = saveRecipeAndCheckFields(username, password, recipeDTO);

        // update
        recipeDTO.setInstruction(recipeDTO.getInstruction() + " changed");
        recipeDTO.setIngredients(Arrays.asList("broccoli", "salt", "pepper"));
        recipeDTO.setVegetarian(true);
        recipeDTO.setServings(5);

        username = "userB";
        password = "passB";
        String authHeaderStr = "Basic " + Base64Utils.encodeToString((username + ":" + password).getBytes());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(recipeDTO);

        mockMvc.perform(put(String.format(Constants.RECIPES_API_URI, username) + "/" + recipeId)
                        .header("Authorization", authHeaderStr).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    @DisplayName("Scenario 11: User lists his recipes")
    void listOnlyOwnedRecipes() throws Exception {
        String username = "userB";
        String password = "passB";

        RecipeDTO recipeDTO = new RecipeDTO(false, 4, Arrays.asList("chicken", "salt", "pepper"),
                "userB Test instruction");

        Long userBFirstRecipeId = saveRecipeAndCheckFields(username, password, recipeDTO);

        username = "userA";
        password = "passA";

        recipeDTO = new RecipeDTO(false, 5, Arrays.asList("chicken", "salt", "pepper"), "userA Test instruction");

        saveRecipeAndCheckFields(username, password, recipeDTO);

        username = "userB";
        password = "passB";

        recipeDTO = new RecipeDTO(false, 6, Arrays.asList("chicken", "salt", "pepper"), "userB Test instruction2");

        Long userBSecondRecipeId = saveRecipeAndCheckFields(username, password, recipeDTO);

        String authHeaderStr = "Basic " + Base64Utils.encodeToString((username + ":" + password).getBytes());

        mockMvc.perform(get(String.format(Constants.RECIPES_API_URI, username)).header("Authorization", authHeaderStr))
                .andExpect(status().isOk()).andExpect(jsonPath("$.[0].id", is(userBFirstRecipeId.intValue())))
                .andExpect(jsonPath("$.[1].id", is(userBSecondRecipeId.intValue())));
    }

    Long saveRecipeAndCheckFields(String username, String password, RecipeDTO recipeDTO) throws Exception {
        String authHeaderStr = "Basic " + Base64Utils.encodeToString((username + ":" + password).getBytes());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(recipeDTO);

        MvcResult result = mockMvc
                .perform(post(String.format(Constants.RECIPES_API_URI, username)).header("Authorization", authHeaderStr)
                        .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk()).andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.vegetarian", is(recipeDTO.getVegetarian())))
                .andExpect(jsonPath("$.servings", is(recipeDTO.getServings())))
                .andExpect(jsonPath("$.instruction", is(recipeDTO.getInstruction())))
                .andExpect(jsonPath("$.ingredients", is(recipeDTO.getIngredients()))).andReturn();

        String json = result.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(json);

        return jsonObject.getLong("id");
    }

    Long updateRecipeAndCheckFields(String username, String password, RecipeDTO recipeDTO, Long recipeId)
            throws Exception {
        String authHeaderStr = "Basic " + Base64Utils.encodeToString((username + ":" + password).getBytes());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(recipeDTO);

        MvcResult result = mockMvc
                .perform(put(String.format(Constants.RECIPES_API_URI, username) + "/" + recipeId)
                        .header("Authorization", authHeaderStr).contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk()).andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.vegetarian", is(recipeDTO.getVegetarian())))
                .andExpect(jsonPath("$.servings", is(recipeDTO.getServings())))
                .andExpect(jsonPath("$.instruction", is(recipeDTO.getInstruction())))
                .andExpect(jsonPath("$.ingredients", is(recipeDTO.getIngredients()))).andReturn();

        String json = result.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(json);

        return jsonObject.getLong("id");
    }

}
