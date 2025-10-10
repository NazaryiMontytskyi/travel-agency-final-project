package com.epam.finaltask.service;

import com.epam.finaltask.auth.AuthenticationService;
import com.epam.finaltask.auth.dto.*;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.token.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock private UserService userService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDTO = new UserDTO();
        userDTO.setUsername("john");
        userDTO.setPassword("pass");
        userDTO.setRole(Role.USER.name());
    }

    @Test
    void register_ShouldReturnAuthResponseWithToken() {
        when(userService.register(any())).thenReturn(userDTO);
        when(jwtService.generateToken(anyString())).thenReturn("token123");

        var req = new RegisterRequest("john", "pass", "123456");
        var response = authenticationService.register(req);

        assertNotNull(response);
        assertEquals("token123", response.token());
    }

    @Test
    void login_ShouldAuthenticateAndReturnToken() {
        when(userService.getUserByUsername("john")).thenReturn(userDTO);
        when(jwtService.generateToken("john")).thenReturn("jwtToken");

        var req = new LoginRequest("john", "pass");
        var response = authenticationService.login(req);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("jwtToken", response.token());
    }
}
