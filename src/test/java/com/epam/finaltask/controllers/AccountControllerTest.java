package com.epam.finaltask.controllers;

import com.epam.finaltask.auth.CustomUserDetailsService;
import com.epam.finaltask.auth.dto.ChangePasswordRequest;
import com.epam.finaltask.config.SecurityConfig;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import com.epam.finaltask.token.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(SecurityConfig.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private VoucherService voucherService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder encoder;

    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUserDTO = UserDTO.builder()
                .id("a1b2c3d4-e5f6-7890-1234-567890abcdef")
                .username("testuser")
                .balance(1000.0)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void userAccount_ShouldReturnAccountView() throws Exception {
        when(userService.getUserByUsername("testuser")).thenReturn(testUserDTO);

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(view().name("account"))
                .andExpect(model().attribute("user", testUserDTO));
    }

    @Test
    @WithMockUser(username = "testuser")
    void cancelVoucher_ShouldRedirectWithSuccessMessage_OnSuccess() throws Exception {
        doNothing().when(voucherService).cancelOrder(any(String.class), eq("testuser"));

        mockMvc.perform(post("/account/vouchers/some-id/cancel").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void cancelVoucher_ShouldRedirectWithErrorMessage_OnFailure() throws Exception {
        doThrow(new RuntimeException("Cancellation failed")).when(voucherService).cancelOrder(any(String.class), eq("testuser"));

        mockMvc.perform(post("/account/vouchers/some-id/cancel").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void showEditProfileForm_ShouldReturnEditProfileView() throws Exception {
        when(userService.getUserByUsername("testuser")).thenReturn(testUserDTO);

        mockMvc.perform(get("/account/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"))
                .andExpect(model().attributeExists("user", "passwordRequest"));
    }


    @Test
    @WithMockUser(username = "testuser")
    void changePassword_ShouldRedirectToLogout_OnSuccess() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass");
        when(userService.getUserByUsername("testuser")).thenReturn(testUserDTO);
        when(userService.changePassword(testUserDTO.getId(), request)).thenReturn(Optional.of(testUserDTO));

        mockMvc.perform(post("/account/change-password")
                        .with(csrf())
                        .flashAttr("passwordRequest", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logout"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void changePassword_ShouldRedirectToEdit_OnFailure() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("wrongOldPass", "newPass");
        when(userService.getUserByUsername("testuser")).thenReturn(testUserDTO);
        when(userService.changePassword(testUserDTO.getId(), request)).thenReturn(Optional.empty());

        mockMvc.perform(post("/account/change-password")
                        .with(csrf())
                        .flashAttr("passwordRequest", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/edit"))
                .andExpect(flash().attributeExists("passwordError"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void processDeposit_ShouldRedirectToAccount_OnSuccess() throws Exception {
        mockMvc.perform(post("/account/deposit")
                        .with(csrf())
                        .param("amount", "100.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void processDeposit_ShouldRedirectToDeposit_WhenAmountIsInvalid() throws Exception {
        mockMvc.perform(post("/account/deposit")
                        .with(csrf())
                        .param("amount", "-50.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/deposit"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}