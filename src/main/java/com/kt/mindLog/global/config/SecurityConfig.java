package com.kt.mindLog.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kt.mindLog.global.security.JwtFilter;
import com.kt.mindLog.global.security.JwtProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	private static final String[] GET_PERMIT_ALL = {"/actuator/**", "/v1/auth/**"};
	private static final String[] POST_PERMIT_ALL = {"/"};
	private static final String[] PATCH_PERMIT_ALL = {"/"};
	private static final String[] PUT_PERMIT_ALL = {"/"};

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, JwtProvider jwtProvider) throws Exception {
		http.sessionManagement(
				session ->
					session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(
				request -> {
					request.requestMatchers(HttpMethod.GET, GET_PERMIT_ALL).permitAll();
					request.requestMatchers(HttpMethod.POST, POST_PERMIT_ALL).permitAll();
					request.requestMatchers(HttpMethod.PATCH, PATCH_PERMIT_ALL).permitAll();
					request.requestMatchers(HttpMethod.PUT, PUT_PERMIT_ALL).permitAll();
					request.anyRequest().authenticated();
				}
			)
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			.csrf(AbstractHttpConfigurer::disable);

		return http.build();
	}
}
