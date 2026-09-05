package com.asdin.notification.config;

import com.asdin.notification.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** Allows only internal event routes and authenticated history reads. */
@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filter(HttpSecurity http, JwtAuthenticationFilter jwt) throws Exception {
        return http.csrf(c -> c.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a.requestMatchers("/actuator/health", "/api/notify/outbid",
                        "/api/notify/winner", "/api/notify/ended").permitAll().anyRequest().authenticated())
                .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class).build();
    }
}
