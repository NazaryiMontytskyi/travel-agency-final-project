package com.epam.finaltask.restcontroller;

import com.epam.finaltask.auth.AuthenticationService;
import com.epam.finaltask.auth.dto.AuthResponse;
import com.epam.finaltask.auth.dto.LoginRequest;
import com.epam.finaltask.auth.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns an access token.")
    @ApiResponse(responseCode = "201", description = "User registered successfully",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class)) })
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(registerRequest));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates an existing user and returns an access token.")
    @ApiResponse(responseCode = "200", description = "Successful login",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class)) })
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }

}
