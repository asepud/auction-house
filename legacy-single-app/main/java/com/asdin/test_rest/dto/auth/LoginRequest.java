package com.asdin.test_rest.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

/** Credentials used to receive a JWT. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
}
