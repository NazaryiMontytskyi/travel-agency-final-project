package com.epam.finaltask.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

public enum Role {
    ADMIN, MANAGER, USER;

    private Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getPermissions(){
        return permissions.stream().map(perm -> new SimpleGrantedAuthority(perm.getPermission())).toList();
    }

    public static Role fromString(String role){
        if(role == null || role.isEmpty()){
            throw new IllegalArgumentException("Role cannot be null or empty");
        }

        return switch (role) {
            case "admin" -> Role.ADMIN;
            case "manager" -> Role.MANAGER;
            case "user" -> Role.USER;
            default -> throw new IllegalArgumentException("Invalid role");
        };
    }
}
