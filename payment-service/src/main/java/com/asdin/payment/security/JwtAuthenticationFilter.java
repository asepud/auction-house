package com.asdin.payment.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Validates the shared JWT so winners can access their invoices directly. */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final SecretKey key;

    public JwtAuthenticationFilter(@Value("${app.jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String v = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (v != null && v.startsWith("Bearer "))
            try {
                Claims c = Jwts.parser().verifyWith(key).build().parseSignedClaims(v.substring(7)).getPayload();
                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(c.getSubject(), null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + c.get("role", String.class)))));
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        chain.doFilter(req, res);
    }
}
