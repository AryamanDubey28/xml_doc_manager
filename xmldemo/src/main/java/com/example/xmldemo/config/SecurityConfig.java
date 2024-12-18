package com.example.xmldemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF for now since we're just testing
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/**").permitAll()  // Allow all requests to /api/**
                .anyRequest().authenticated()
            );
            
        return http.build();
    }
}
