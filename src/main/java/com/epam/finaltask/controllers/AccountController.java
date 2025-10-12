package com.epam.finaltask.controllers;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @GetMapping("/account")
    @PreAuthorize("isAuthenticated()")
    public String userAccount(Model model, Principal principal) {
        UserDTO user = userService.getUserByUsername(principal.getName());
        model.addAttribute("user", user);
        return "account";
    }

    @GetMapping("/manager/account")
    @PreAuthorize("hasAuthority('manager:update')")
    public String managerAccount(Model model, Principal principal) {
        UserDTO user = userService.getUserByUsername(principal.getName());
        model.addAttribute("user", user);
        return "manager-account";
    }

    @GetMapping("/admin/panel")
    @PreAuthorize("hasAuthority('admin:read')")
    public String adminPanel(Model model, Principal principal) {
        UserDTO user = userService.getUserByUsername(principal.getName());
        model.addAttribute("user", user);
        return "admin-panel";
    }
}