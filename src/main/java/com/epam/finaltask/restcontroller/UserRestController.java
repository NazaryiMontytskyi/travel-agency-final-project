package com.epam.finaltask.restcontroller;

import com.epam.finaltask.auth.dto.ChangePasswordRequest;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:update') or @userSecurity.isOwnerById(#id)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id){
        return ResponseEntity.ok(this.userService.getUserById(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:update') or @userSecurity.isOwnerById(#id)")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @Valid @RequestBody UserDTO userDTO){
        if(this.userService.existsById(UUID.fromString(id))){
            var username = this.userService.getUserById(UUID.fromString(id)).getUsername();
            var result = this.userService.updateUser(username, userDTO);
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAuthority('admin:update') or @userSecurity.isOwnerByUsername(#username)")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(this.userService.getUserByUsername(username));
    }

    @PatchMapping("/block/{id}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<UserDTO> blockUser(@PathVariable String id){
        return userService.blockUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/unblock/{id}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<UserDTO> unblockUser(@PathVariable String id){
        return userService.unblockUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/password/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<UserDTO> changePassword(@PathVariable String id, @Valid @RequestBody ChangePasswordRequest request){
        return this.userService.changePassword(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
	
}
