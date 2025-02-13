package com.accenture.backend.config.security;

import com.accenture.backend.service.OAuth2Service;
import com.accenture.backend.util.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;


import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final OAuth2Service oauth2Service;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(request -> {
                                        CorsConfiguration config = new CorsConfiguration();
                                        config.setAllowedOrigins(
                                                        Arrays.asList("http://localhost:3000"));

                                        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE",
                                                        "OPTIONS"));
                                        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                                        config.setAllowCredentials(true);
                                        return config;
                                }))
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/v1/login/**", "/api/v1/sign-up/**",
                                                                "/api/v1/oauth2/**",
                                                                "/api/v1/payments/webhook",
                                                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                                                .permitAll()
                                                .requestMatchers("/api/v1/identity/**")
                                                .authenticated()
                                                .requestMatchers("/api/v1/user/email-request",
                                                                "/api/v1/user/email-code/**")
                                                .hasRole("NOT_CONFIRMED")
                                                .requestMatchers("/api/v1/user/**", "/api/v1/projects/**",
                                                                "/api/v1/tasks/**", "/api/v1/notifications/**",
                                                                "/api/v1/payments/create-checkout-session")
                                                .hasRole("USER")
                                                .requestMatchers("/api/v1/admin-dashboard/**").hasRole("ADMIN")
                                                .requestMatchers("/api/v1/moderator-dashboard/**").hasRole("MODERATOR")
                                                .anyRequest()
                                                .denyAll())
                                .oauth2Login(oauth2 -> oauth2
                                                .successHandler(oauth2Service::handleOAuth2Success))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
