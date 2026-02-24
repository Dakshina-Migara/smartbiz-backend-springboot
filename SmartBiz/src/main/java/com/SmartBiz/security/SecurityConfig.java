package com.SmartBiz.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SecurityConfig - Spring Security configuration for the SmartBiz application.
 *
 * Configures:
 * - CORS (Cross-Origin Resource Sharing) to allow frontend connections
 * - CSRF disabled for REST API compatibility
 * - All requests permitted (development mode)
 */
@Configuration
public class SecurityConfig {

    /**
     * Security filter chain — configures HTTP security rules.
     * CORS is enabled so the frontend (e.g., React on localhost:3000) can call the
     * API.
     * CSRF is disabled since REST APIs use token-based auth, not cookies.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Enable CORS using the corsConfigurationSource bean defined below
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Disable CSRF protection (not needed for stateless REST APIs)
                .csrf(AbstractHttpConfigurer::disable)
                // Allow all requests without authentication (development mode)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());

        return http.build();
    }

    /**
     * CORS Configuration — defines which origins, methods, and headers are allowed.
     *
     * Without CORS, the browser blocks requests from a different origin
     * (e.g., frontend on localhost:3000 calling API on localhost:8080).
     *
     * This configuration allows:
     * - Origins: localhost:3000 and localhost:5173 (React / Vite dev servers)
     * - Methods: GET, POST, PUT, DELETE, OPTIONS
     * - Headers: All headers
     * - Credentials: Cookies and auth headers are allowed
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allowed frontend origins (add your production URL here later)
        config.setAllowedOrigins(List.of(
                "http://localhost:3000", // React default dev server
                "http://localhost:5173" // Vite default dev server
        ));

        // Allowed HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers (e.g., Authorization, Content-Type)
        config.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Apply this CORS configuration to all API endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}