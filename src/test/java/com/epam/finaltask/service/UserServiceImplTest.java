package com.epam.finaltask.service;

import com.epam.finaltask.auth.dto.ChangePasswordRequest;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPassword("encodedPass");
        user.setActive(true);
        user.setRole(Role.USER);
        user.setBalance(BigDecimal.valueOf(100));

        userDTO = new UserDTO();
        userDTO.setId(user.getId().toString());
        userDTO.setUsername("testuser");
        userDTO.setPassword("rawPass");
        userDTO.setRole("USER");
        userDTO.setBalance(100.0);
    }

    @Test
    void register_ShouldEncodePasswordAndSaveUser() {
        when(userMapper.toUser(any())).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserDTO(any())).thenReturn(userDTO);

        UserDTO result = userService.register(userDTO);

        assertNotNull(result);
        verify(passwordEncoder).encode("rawPass");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserDTO(any(User.class));
    }

    @Test
    void getUserByUsername_ShouldReturnUserDTO_WhenExists() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserByUsername("testuser");

        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserByUsername_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByUsername("unknown"));
    }

    @Test
    void blockUser_ShouldDeactivateAndSaveUser() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(any())).thenReturn(userDTO);

        var result = userService.blockUser(user.getId().toString());

        assertTrue(result.isPresent());
        verify(userRepository).save(any(User.class));
        assertFalse(user.isActive());
    }

    @Test
    void changePassword_ShouldUpdatePassword_WhenOldPasswordMatches() {
        ChangePasswordRequest req = new ChangePasswordRequest("old", "new");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("old")).thenReturn("encodedPass");
        when(passwordEncoder.encode("new")).thenReturn("encodedNew");
        when(userMapper.toUserDTO(any())).thenReturn(userDTO);

        var result = userService.changePassword(user.getId().toString(), req);

        assertTrue(result.isPresent());
        verify(userRepository).save(user);
    }
}