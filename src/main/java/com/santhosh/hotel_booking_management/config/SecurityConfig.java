package com.santhosh.hotel_booking_management.config;

import com.santhosh.hotel_booking_management.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register","/api/auth/login",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()
                        // HOTEL - ADMIN ONLY
                        .requestMatchers(HttpMethod.POST, "/api/hotels")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/hotels/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/hotels/**")
                        .hasRole("ADMIN")

                        // ROOM - ADMIN ONLY
                        .requestMatchers(HttpMethod.POST, "/api/rooms")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/rooms/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/rooms/**")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
