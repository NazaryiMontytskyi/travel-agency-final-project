package com.epam.finaltask.auth;

import com.epam.finaltask.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserService userService;

    public boolean isOwnerById(String id) {
        if(id == null){
            return false;
        }
        String authName = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = this.userService.getUserById(UUID.fromString(id));
        var username = user.getUsername();
        return authName.equals(username);
    }

    public boolean isOwnerByUsername(String username) {
        if(username == null){
            return false;
        }
        String authName = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = this.userService.getUserByUsername(username);
        return authName.equals(user.getUsername());
    }
}
