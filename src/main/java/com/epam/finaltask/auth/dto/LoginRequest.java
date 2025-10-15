package com.epam.finaltask.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotEmpty
        @NotBlank
        @Size(min = 4, max = 30)
        String username,
        @NotEmpty
        @NotBlank
        @Size(min = 6, max = 100, message = "Password must be from 6 to 100 characters long")
        String password) {
}
