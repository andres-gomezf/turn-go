package com.turngo.turngo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())              // deshabilita CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()             // permite todos los endpoints
                );
        return http.build();
    }
}

// Este archivo lo cree para poder deshabilitar el tema de credenciales para las requests. Luego hay que borrarlo o cambiarlo.