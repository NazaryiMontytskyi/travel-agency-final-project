package com.epam.finaltask.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static com.epam.finaltask.model.Permission.*;

import java.util.List;
import java.util.Set;

public enum Role {
    ADMIN(Set.of(ADMIN_READ, ADMIN_UPDATE, ADMIN_DELETE, ADMIN_CREATE)),
    MANAGER(Set.of(USER_CREATE, USER_DELETE, USER_UPDATE, USER_READ, MANAGER_UPDATE)),
    USER(Set.of(USER_READ, USER_UPDATE, USER_DELETE, USER_CREATE));

    private Set<Permission> permissions;

    private Role(Set<Permission> permissions){
        this.permissions = permissions;
    }

    public List<SimpleGrantedAuthority> getPermissions(){
        return permissions.stream().map(perm -> new SimpleGrantedAuthority(perm.getPermission())).toList();
    }

}
