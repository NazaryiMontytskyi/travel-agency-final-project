package com.epam.finaltask.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ChangePasswordRequest(
        @NotEmpty
        @NotBlank
        String oldPassword,
        @NotEmpty
        @NotBlank
        String newPassword) {
}
