package com.crediteuropebank.recipeapi;

import com.crediteuropebank.recipeapi.model.dto.RecipeDTO;
import com.crediteuropebank.recipeapi.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ValidationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test()
    @DisplayName("Scenario 1: User tries to create recipe without instruction")
    void instructionNullOrEmpty() throws Exception {
        String username = "userA";
        String password = "passA";
        // null
        RecipeDTO recipeDTO = new RecipeDTO(false, 4, Arrays.asList("chicken", "salt", "pepper"), null);
        checkValidationRule(username, password, recipeDTO, Constants.INSTRUCTION_EMPTY_ERR_MSG);
        // empty
        recipeDTO = new RecipeDTO(false, 4, Arrays.asList("chicken", "salt", "pepper"), "");
        checkValidationRule(username, password, recipeDTO, Constants.INSTRUCTION_EMPTY_ERR_MSG);
    }

    @Test
    @DisplayName("Scenario 2: User tries to create recipe without ingredients")
    void ingredientsNullOrEmpty() throws Exception {
        String username = "userA";
        String password = "passA";
        // null
        RecipeDTO recipeDTO = new RecipeDTO(false, 4, null, "Test instruction");
        checkValidationRule(username, password, recipeDTO, Constants.INGREDIENTS_EMPTY_ERR_MSG);
        // empty
        recipeDTO = new RecipeDTO(false, 4, List.of(), "Test instruction");
        checkValidationRule(username, password, recipeDTO, Constants.INGREDIENTS_EMPTY_ERR_MSG);
    }

    @Test
    @DisplayName("Scenario 3: User tries to create recipe without servings amount")
    void servingsNull() throws Exception {
        String username = "userA";
        String password = "passA";

        RecipeDTO recipeDTO = new RecipeDTO(false, null, Arrays.asList("chicken", "salt", "pepper"),
                "Fry chicken with salt and pepper");
        checkValidationRule(username, password, recipeDTO, Constants.SERVINGS_NULL_ERR_MSG);
    }

    @Test
    @DisplayName("Scenario 4: User tries to create recipe for 0 people (servings=0)")
    void servingsMinCheck() throws Exception {
        String username = "userB";
        String password = "passB";

        RecipeDTO recipeDTO = new RecipeDTO(false, 0, Arrays.asList("chicken", "salt", "pepper"),
                "Fry chicken with salt and pepper");
        checkValidationRule(username, password, recipeDTO, Constants.SERVINGS_MIN_ERR_MSG);
    }

    @Test
    @DisplayName("Scenario 5: User tries to create recipe without vegetarian info")
    void vegetarianNull() throws Exception {
        String username = "userB";
        String password = "passB";

        RecipeDTO recipeDTO = new RecipeDTO(null, 4, Arrays.asList("salt", "pepper"),
                "Fry chicken with salt and pepper");
        checkValidationRule(username, password, recipeDTO, Constants.VEGETARIAN_NULL_ERR_MSG);
    }

    @Test
    @DisplayName("Scenario 6: User gives wrong username or password")
    void wrongUsernameAndPassword() throws Exception {
        String username = "userB";
        String password = "passA";

        RecipeDTO recipeDTO = new RecipeDTO(null, 4, Arrays.asList("salt", "pepper"),
                "Fry chicken with salt and pepper");

        String authHeaderStr = "Basic " + Base64Utils.encodeToString((username + ":" + password).getBytes());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(recipeDTO);

        mockMvc.perform(post(String.format(Constants.RECIPES_API_URI, username)).header("Authorization", authHeaderStr)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson)).andExpect(status().is4xxClientError());
    }

    void checkValidationRule(String username, String password, RecipeDTO recipeDTO, String expectedErrMsg)
            throws Exception {
        String authHeaderStr = "Basic " + Base64Utils.encodeToString((username + ":" + password).getBytes());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(recipeDTO);

        mockMvc.perform(post(String.format(Constants.RECIPES_API_URI, username)).header("Authorization", authHeaderStr)
                        .contentType(MediaType.APPLICATION_JSON).content(requestJson)).andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors").isArray()).andExpect(jsonPath("$.errors", hasItem(expectedErrMsg)));
    }
}
