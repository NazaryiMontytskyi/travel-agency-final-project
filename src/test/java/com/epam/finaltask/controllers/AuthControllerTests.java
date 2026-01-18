package com.epam.finaltask.controllers;

import com.epam.finaltask.auth.AuthenticationService;
import com.epam.finaltask.auth.CustomUserDetailsService;
import com.epam.finaltask.auth.dto.RegisterRequest;
import com.epam.finaltask.config.SecurityConfig;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.token.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder encoder;

    @Test
    void login_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void showRegistrationForm_ShouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void registerUser_ShouldRedirectToLogin_OnSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "password123", "1234567890");
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .flashAttr("registerRequest", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("registrationSuccess", true));
    }

    @Test
    void registerUser_ShouldReturnRegisterView_WhenUsernameExists() throws Exception {
        RegisterRequest request = new RegisterRequest("existinguser", "password123", "test@test.com");
        doThrow(new RuntimeException("User exists")).when(authenticationService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .flashAttr("registerRequest", request))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }
}
