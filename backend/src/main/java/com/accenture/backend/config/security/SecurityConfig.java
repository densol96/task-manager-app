package com.accenture.backend.config.security;

import com.accenture.backend.util.JwtAuthenticationFilter;
import com.accenture.backend.util.JwtChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtChannelInterceptor jwtChannelInterceptor;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login/**", "/sign-up/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/user/email-request").hasRole("NOT_CONFIRMED")
                        .requestMatchers("/api/v1/user/email-code/**").hasRole("NOT_CONFIRMED")
                        .requestMatchers("/api/v1/user/**").hasRole("USER")
                        .requestMatchers("/api/v1/tasks/**").hasRole("USER")
                        .requestMatchers("/api/v1/notifications/**").hasRole("USER")
                        .requestMatchers("/api/v1/report/**").hasRole("USER")
                        .requestMatchers("/api/v1/admin-dashboard/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/moderator-dashboard/**").hasRole("MODERATOR")
                        .requestMatchers("/topic/**").hasRole("MODERATOR")
                        .anyRequest().denyAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    public void customizeClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
}
