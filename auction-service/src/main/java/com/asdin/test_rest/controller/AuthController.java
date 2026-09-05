package com.asdin.test_rest.controller;

import com.asdin.test_rest.dto.auth.*;
import com.asdin.test_rest.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/** Public registration and login endpoints. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auth.register(r));
    }

    @PostMapping("/login")
    AuthResponse login(@Valid @RequestBody LoginRequest r) {
        return auth.login(r);
    }
}
