package com.epam.finaltask.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @NotNull String username,
        @NotBlank @NotNull @Size(min = 6, max = 100, message = "Password must be from 6 to 100 characters long")
        String password,
        @NotBlank @NotNull @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number format is invalid")
        String phoneNumber) { }