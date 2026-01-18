package com.epam.finaltask.controllers;

import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final VoucherService voucherService;

    @GetMapping("/payment/{id}")
    @PreAuthorize("isAuthenticated()")
    public String paymentForm(@PathVariable String id, Model model) {
        model.addAttribute("tour", voucherService.findById(id));
        return "payment";
    }

    @PostMapping("/payment/{id}")
    @PreAuthorize("isAuthenticated()")
    public String processPayment(@PathVariable String id, RedirectAttributes redirectAttributes) {
        voucherService.changeVoucherStatus(id, "PAID");

        redirectAttributes.addFlashAttribute("paymentSuccess", "Payment successful! Your tour is confirmed.");
        return "redirect:/";
    }
}