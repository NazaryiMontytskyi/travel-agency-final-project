package com.epam.finaltask.controllers;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class TourController {

    private final VoucherService voucherService;
    private final UserService userService;

    @GetMapping("/tours/{id}")
    public String watchTour(@PathVariable String id, Model model) {
        model.addAttribute("tour", voucherService.findById(id));
        return "tour-details";
    }

    @GetMapping("/order/{id}")
    @PreAuthorize("isAuthenticated()")
    public String orderTourForm(@PathVariable String id, Model model) {
        model.addAttribute("tour", voucherService.findById(id));
        return "order-confirmation";
    }

    @PostMapping("/order/{id}")
    @PreAuthorize("isAuthenticated()")
    public String processOrder(@PathVariable String id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            UserDTO currentUser = userService.getUserByUsername(principal.getName());
            VoucherDTO voucher = voucherService.findById(id);

            if (currentUser.getBalance() < voucher.getPrice()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Insufficient funds to order this tour. Please top up your balance.");
                return "redirect:/tours/" + id;
            }

            voucherService.order(id, currentUser.getId());
            return "redirect:/payment/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to order tour: " + e.getMessage());
            return "redirect:/tours/" + id;
        }
    }
}