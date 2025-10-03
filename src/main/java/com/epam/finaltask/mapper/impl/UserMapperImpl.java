package com.epam.finaltask.mapper.impl;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        UUID userId = null;
        if (userDTO.getId() != null) {
            try {
                userId = UUID.fromString(userDTO.getId());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID for User: " + userDTO.getId(), e);
            }
        }

        return User.builder()
                .id(userId)
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .role(Role.fromString(userDTO.getRole()))
                .vouchers(Optional.ofNullable(userDTO.getVouchers()).orElse(Collections.emptyList()))
                .phoneNumber(userDTO.getPhoneNumber())
                .balance(Optional.of(BigDecimal.valueOf(userDTO.getBalance())).orElse(BigDecimal.valueOf(0.0)))
                .active(userDTO.isActive())
                .build();
    }

    @Override
    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId() != null ? user.getId().toString() : null)
                .username(user.getUsername())
                .role(user.getRole() != null ? user.getRole().toString() : null)
                .vouchers(Optional.ofNullable(user.getVouchers()).orElse(Collections.emptyList()))
                .phoneNumber(user.getPhoneNumber())
                .balance(user.getBalance() != null ? user.getBalance().doubleValue() : 0.0)
                .active(user.isActive())
                .build();
    }
}
