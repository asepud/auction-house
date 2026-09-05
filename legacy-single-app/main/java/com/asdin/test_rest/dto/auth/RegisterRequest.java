package com.asdin.test_rest.dto.auth;

import com.asdin.test_rest.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;

/** Public registration input. ADMIN is never accepted from this endpoint. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotBlank
    @Email
    @Size(max = 150)
    private String email;
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
    private Role role;
}
