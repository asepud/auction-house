package com.asdin.test_rest.dto.auth;

import com.asdin.test_rest.enums.Role;
import lombok.*;

/** Authenticated identity and bearer token. */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String name;
    private String email;
    private Role role;
}
