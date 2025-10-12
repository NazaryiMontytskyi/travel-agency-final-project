package com.epam.finaltask.controllers;

import com.epam.finaltask.auth.dto.ChangePasswordRequest;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;
    private final VoucherService voucherService;

    @GetMapping("/account")
    @PreAuthorize("isAuthenticated()")
    public String userAccount(Model model, Principal principal) {
        UserDTO user = userService.getUserByUsername(principal.getName());
        model.addAttribute("user", user);
        return "account";
    }

    @PostMapping("/account/vouchers/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public String cancelVoucher(@PathVariable String id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            voucherService.cancelOrder(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Tour canceled and funds returned.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error canceling tour: " + e.getMessage());
        }
        return "redirect:/account";
    }

    @GetMapping("/account/edit")
    @PreAuthorize("isAuthenticated()")
    public String showEditProfileForm(Model model, Principal principal) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", userService.getUserByUsername(principal.getName()));
        }
        if (!model.containsAttribute("passwordRequest")) {
            model.addAttribute("passwordRequest", new ChangePasswordRequest("", ""));
        }
        return "edit-profile";
    }

    @PostMapping("/account/edit")
    @PreAuthorize("isAuthenticated()")
    public String updateUserProfile(@Valid @ModelAttribute("user") UserDTO userDTO,
                                    BindingResult bindingResult,
                                    Principal principal,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordRequest", new ChangePasswordRequest("", ""));
            return "edit-profile";
        }
        userService.updateUser(principal.getName(), userDTO);
        return "redirect:/account?profile_updated";
    }

    @PostMapping("/account/change-password")
    @PreAuthorize("isAuthenticated()")
    public String changePassword(@Valid @ModelAttribute("passwordRequest") ChangePasswordRequest passwordRequest,
                                 BindingResult bindingResult,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordRequest", bindingResult);
            redirectAttributes.addFlashAttribute("passwordRequest", passwordRequest);
            return "redirect:/account/edit";
        }
        try {
            UserDTO user = userService.getUserByUsername(principal.getName());
            userService.changePassword(user.getId(), passwordRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully! Please log in again.");
            return "redirect:/logout";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("passwordError", "Error changing password: " + e.getMessage());
            return "redirect:/account/edit";
        }
    }

    @GetMapping("/account/deposit")
    @PreAuthorize("isAuthenticated()")
    public String showDepositForm() {
        return "deposit";
    }

    @PostMapping("/account/deposit")
    @PreAuthorize("isAuthenticated()")
    public String processDeposit(@RequestParam("amount") Double amount, Principal principal, RedirectAttributes redirectAttributes) {
        if (amount == null || amount <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Amount must be positive.");
            return "redirect:/account/deposit";
        }
        try {
            userService.updateUserBalance(principal.getName(), amount);
            redirectAttributes.addFlashAttribute("successMessage", "Balance updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating balance.");
        }
        return "redirect:/account";
    }

}