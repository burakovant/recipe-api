package com.crediteuropebank.recipeapi.util;

import com.crediteuropebank.recipeapi.data.entity.User;
import com.crediteuropebank.recipeapi.data.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initCategories(UserRepository repository) {

        return args -> {
            log.info("Loading User userA: {}", repository.save(new User("userA")));
            log.info("Loading User userB: {}", repository.save(new User("userB")));
        };
    }

}