package com.asdin.test_rest.security;

import com.asdin.test_rest.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/** Issues and validates HMAC-signed access tokens. */
@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) throw new IllegalArgumentException("JWT secret must contain at least 32 bytes");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }
    public String generate(User user) {
        Instant now = Instant.now();
        return Jwts.builder().subject(String.valueOf(user.getId())).claim("role", user.getRole().name())
                .claim("email", user.getEmail()).issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationMinutes * 60))).signWith(key).compact();
    }
    public Claims parse(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
}
