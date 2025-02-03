package com.crediteuropebank.recipeapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        List<SecurityScheme> schemeList = new ArrayList<>();
        schemeList.add(new BasicAuth("basicAuth"));

        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.crediteuropebank.recipeapi.controller"))
                .paths(PathSelectors.regex("/.*")).build().apiInfo(apiEndPointsInfo()).securitySchemes(schemeList);

    }

    private ApiInfo apiEndPointsInfo() {
        return new ApiInfoBuilder().title("Recipe Api Documentation").description("Recipe Api Documentation")
                .contact(new Contact("Burak Sezin Ovant", "https://github.com/burakovant", "burakovant@gmail.com"))
                .license("Apache 2.0").licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html").version("0.0.1")
                .build();
    }

}