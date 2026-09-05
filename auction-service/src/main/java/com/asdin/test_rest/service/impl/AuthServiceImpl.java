package com.asdin.test_rest.service.impl;

import com.asdin.test_rest.domain.User;
import com.asdin.test_rest.dto.auth.*;
import com.asdin.test_rest.enums.Role;
import com.asdin.test_rest.exception.BusinessException;
import com.asdin.test_rest.repository.UserRepository;
import com.asdin.test_rest.security.JwtService;
import com.asdin.test_rest.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Default auth implementation using BCrypt and signed JWTs. */
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthServiceImpl(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public AuthResponse register(RegisterRequest r) {
        if (users.existsByEmailIgnoreCase(r.getEmail()))
            throw new BusinessException(HttpStatus.CONFLICT, "Email already registered");
        Role role = r.getRole() == Role.SELLER ? Role.SELLER : Role.BIDDER;
        User u = users.save(User.builder().name(r.getName().trim()).email(r.getEmail().trim().toLowerCase())
                .password(encoder.encode(r.getPassword())).role(role).build());
        return response(u);
    }

    public AuthResponse login(LoginRequest r) {
        User u = users.findByEmailIgnoreCase(r.getEmail())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!encoder.matches(r.getPassword(), u.getPassword()))
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        return response(u);
    }

    private AuthResponse response(User u) {
        return AuthResponse.builder().token(jwt.generate(u)).tokenType("Bearer").userId(u.getId()).name(u.getName())
                .email(u.getEmail()).role(u.getRole()).build();
    }
}
