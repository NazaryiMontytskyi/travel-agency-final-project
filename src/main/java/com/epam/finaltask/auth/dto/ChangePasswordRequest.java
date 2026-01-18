package com.epam.finaltask.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotEmpty
        @NotBlank
        @Size(min = 6, max = 100, message = "Password must be from 6 to 100 characters long")
        String oldPassword,
        @NotEmpty
        @NotBlank
        @Size(min = 6, max = 100, message = "Password must be from 6 to 100 characters long")
        String newPassword) {
}
