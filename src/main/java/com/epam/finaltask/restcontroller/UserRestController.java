package com.epam.finaltask.restcontroller;

import com.epam.finaltask.auth.dto.ChangePasswordRequest;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API for managing users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:update') or @userSecurity.isOwnerById(#id)")
    @Operation(summary = "Get a user by ID", description = "Returns a single user by their unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id){
        return ResponseEntity.ok(this.userService.getUserById(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:update') or @userSecurity.isOwnerById(#id)")
    @Operation(summary = "Update a user", description = "Updates the details of an existing user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
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
    @Operation(summary = "Get a user by username", description = "Returns a single user by their username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(this.userService.getUserByUsername(username));
    }

    @PatchMapping("/block/{id}")
    @PreAuthorize("hasAuthority('admin:update')")
    @Operation(summary = "Block a user", description = "Blocks a user's account, preventing them from logging in.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User blocked successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    public ResponseEntity<UserDTO> blockUser(@PathVariable String id){
        return userService.blockUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/unblock/{id}")
    @PreAuthorize("hasAuthority('admin:update')")
    @Operation(summary = "Unblock a user", description = "Unblocks a user's account, allowing them to log in again.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User unblocked successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    public ResponseEntity<UserDTO> unblockUser(@PathVariable String id){
        return userService.unblockUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/password/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    @Operation(summary = "Change user password", description = "Allows a user to change their own password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    public ResponseEntity<UserDTO> changePassword(@PathVariable String id, @Valid @RequestBody ChangePasswordRequest request){
        return this.userService.changePassword(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
	
}
