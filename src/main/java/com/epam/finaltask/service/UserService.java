package com.epam.finaltask.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.epam.finaltask.auth.dto.ChangePasswordRequest;
import com.epam.finaltask.dto.UserDTO;

public interface UserService {
    UserDTO register(UserDTO userDTO);

    UserDTO updateUser(String username, UserDTO userDTO);

    UserDTO getUserByUsername(String username);
    UserDTO changeAccountStatus(UserDTO userDTO);
    UserDTO getUserById(UUID id);
    boolean existsById(UUID id);
    Optional<UserDTO> blockUser(String id);
    Optional<UserDTO> unblockUser(String id);
    Optional<UserDTO> changePassword(String id, ChangePasswordRequest newPassword);
    List<UserDTO> findAll();
    void updateUserBalance(String username, Double amountChange);
}
