package com.crediteuropebank.recipeapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] AUTH_WHITELIST = { "/v2/api-docs", "/swagger-resources/configuration/ui",
			"/swagger-resources", "/swagger-resources/configuration/security", "/swagger-ui.html",
			"/swagger-ui/**", "/webjars/**", "/h2-console/**"};

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll().and().authorizeRequests().anyRequest()
				.authenticated().and().csrf().disable().httpBasic().and().headers().frameOptions().sameOrigin();
		http.cors();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("userA").password(passwordEncoder().encode("passA")).authorities("USER");
		auth.inMemoryAuthentication().withUser("userB").password(passwordEncoder().encode("passB")).authorities("USER");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
