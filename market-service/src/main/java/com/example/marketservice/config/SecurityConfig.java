package com.example.marketservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.marketservice.service.MarketService;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{
    private final JwtConverter jwtConverter;
    private final MarketService marketService;

    public SecurityConfig(MarketService marketService, JwtConverter jwtConverter) {
        this.jwtConverter = jwtConverter;
        this.marketService = marketService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize)-> authorize
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers("/api/market/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(
                        (oauth2)-> oauth2.jwt(
                                jwt-> jwt.jwtAuthenticationConverter(jwtConverter)
                        ))
                .sessionManagement(
                        session-> session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
