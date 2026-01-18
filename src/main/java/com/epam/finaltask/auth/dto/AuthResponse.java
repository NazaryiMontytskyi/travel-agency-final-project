package com.epam.finaltask.auth.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthResponse(
        @NotEmpty
        String token
) { }
