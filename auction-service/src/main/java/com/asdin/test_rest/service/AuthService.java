package com.asdin.test_rest.service;

import com.asdin.test_rest.dto.auth.*;

/** Authentication use cases. */
public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
