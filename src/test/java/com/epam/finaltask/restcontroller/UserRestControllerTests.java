package com.epam.finaltask.restcontroller;

import com.epam.finaltask.auth.CustomUserDetailsService;
import com.epam.finaltask.auth.dto.ChangePasswordRequest;
import com.epam.finaltask.auth.UserSecurity;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.token.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
@EnableMethodSecurity
@Import(UserSecurity.class)
public class UserRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserSecurity userSecurity;

    private UserDTO createTestUserDTO(UUID id, String username) {
        return UserDTO.builder()
                .id(id.toString())
                .username(username)
                .password("password")
                .role("USER")
                .active(true)
                .phoneNumber("+380123456789")
                .balance(100.0)
                .build();
    }


    @Nested
    @DisplayName("Tests for getUserById (GET /api/users/{id})")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user when called by admin")
        @WithMockUser(authorities = "admin:update")
        void shouldReturnUserForAdmin() throws Exception {
            UUID userId = UUID.randomUUID();
            UserDTO user = createTestUserDTO(userId, "testuser");
            when(userService.getUserById(userId)).thenReturn(user);

            mockMvc.perform(get("/api/users/{id}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId.toString()))
                    .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("Should return user when called by owner")
        @WithMockUser(username = "testuser")
        void shouldReturnUserForOwner() throws Exception {
            UUID userId = UUID.randomUUID();
            String userIdStr = userId.toString();
            UserDTO user = createTestUserDTO(userId, "testuser");

            when(userSecurity.isOwnerById(userIdStr)).thenReturn(true);
            when(userService.getUserById(userId)).thenReturn(user);

            mockMvc.perform(get("/api/users/{id}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("Should return 403 Forbidden when called by another user")
        @WithMockUser(username = "anotheruser")
        void shouldReturnForbiddenForAnotherUser() throws Exception {
            UUID userId = UUID.randomUUID();
            String userIdStr = userId.toString();

            when(userSecurity.isOwnerById(userIdStr)).thenReturn(false);
            when(userService.getUserById(userId)).thenReturn(createTestUserDTO(userId, "testuser"));

            mockMvc.perform(get("/api/users/{id}", userId))
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    @DisplayName("Tests for updateUser (PATCH /api/users/{id})")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user and return 200 OK")
        @WithMockUser(authorities = "admin:update")
        void shouldUpdateUser() throws Exception {
            UUID userId = UUID.randomUUID();
            UserDTO existingUser = createTestUserDTO(userId, "testuser");
            UserDTO updatedUserRequest = createTestUserDTO(userId, "testuser");
            updatedUserRequest.setPhoneNumber("123456789");

            UserDTO updatedUserResponse = createTestUserDTO(userId, "testuser");
            updatedUserResponse.setPhoneNumber("123456789");

            when(userService.existsById(userId)).thenReturn(true);
            when(userService.getUserById(userId)).thenReturn(existingUser);

            when(userService.updateUser(eq("testuser"), any(UserDTO.class)))
                    .thenReturn(updatedUserResponse);

            mockMvc.perform(patch("/api/users/{id}", userId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedUserRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.phoneNumber").value("123456789"));
        }

        @Test
        @DisplayName("Should return 404 Not Found if user does not exist")
        @WithMockUser(authorities = "admin:update")
        void shouldReturnNotFoundForUpdate() throws Exception {
            UUID userId = UUID.randomUUID();

            UserDTO validDto = createTestUserDTO(userId, "someuser");

            when(userService.existsById(userId)).thenReturn(false);

            mockMvc.perform(patch("/api/users/{id}", userId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Tests for blockUser & unblockUser (PATCH /api/users/...)")
    class BlockUnblockUserTests {

        @Test
        @DisplayName("Should block user and return 200 OK")
        @WithMockUser(authorities = "admin:update")
        void shouldBlockUser() throws Exception {
            UUID userId = UUID.randomUUID();
            UserDTO blockedUser = createTestUserDTO(userId, "testuser");
            blockedUser.setActive(false);

            when(userService.blockUser(userId.toString())).thenReturn(Optional.of(blockedUser));

            mockMvc.perform(patch("/api/users/block/{id}", userId).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.active").value(false));
        }

        @Test
        @DisplayName("Should unblock user and return 200 OK")
        @WithMockUser(authorities = "admin:update")
        void shouldUnblockUser() throws Exception {
            UUID userId = UUID.randomUUID();
            UserDTO unblockedUser = createTestUserDTO(userId, "testuser");
            unblockedUser.setActive(true);

            when(userService.unblockUser(userId.toString())).thenReturn(Optional.of(unblockedUser));

            mockMvc.perform(patch("/api/users/unblock/{id}", userId).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        @DisplayName("Should return 403 Forbidden for non-admin user")
        @WithMockUser(authorities = "user:read")
        void shouldReturnForbiddenForBlock() throws Exception {
            mockMvc.perform(patch("/api/users/block/{id}", UUID.randomUUID()).with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Tests for changePassword (PATCH /api/users/password/{id})")
    class ChangePasswordTests {
        @Test
        @DisplayName("Should change password and return 200 OK")
        @WithMockUser(authorities = "user:update")
        void shouldChangePassword() throws Exception {
            UUID userId = UUID.randomUUID();
            UserDTO user = createTestUserDTO(userId, "testuser");
            ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass");

            when(userService.changePassword(anyString(), any(com.epam.finaltask.auth.dto.ChangePasswordRequest.class))).thenReturn(Optional.of(user));

            mockMvc.perform(patch("/api/users/password/{id}", userId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId.toString()));
        }

        @Test
        @DisplayName("Should return 404 Not Found if user for password change not found")
        @WithMockUser(authorities = "user:update")
        void shouldReturnNotFoundForPasswordChange() throws Exception {
            ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass");
            when(userService.changePassword(anyString(), any(com.epam.finaltask.auth.dto.ChangePasswordRequest.class))).thenReturn(Optional.empty());

            mockMvc.perform(patch("/api/users/password/{id}", UUID.randomUUID())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }
}