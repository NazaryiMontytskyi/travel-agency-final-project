package com.epam.finaltask.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @NotEmpty
        @NotBlank
        String username,
        @NotEmpty
        @NotBlank
        String password) {
}
