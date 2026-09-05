package com.asdin.test_rest.config;

import com.asdin.test_rest.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Stateless security policy for public catalog and authenticated operations.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwt) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health",
                                "/h2-console/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/items/**", "/api/categories/**").permitAll().anyRequest()
                        .authenticated())
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class).build();
    }
}
