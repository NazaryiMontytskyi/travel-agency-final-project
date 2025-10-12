package com.epam.finaltask.controllers;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasAuthority('manager:update')")
@RequiredArgsConstructor
public class ManagerController {

    private final VoucherService voucherService;
    private final UserService userService;

    @GetMapping("/account")
    public String managerDashboard(Model model, Principal principal) {
        var currentUser = this.userService.getUserByUsername(principal.getName());
        model.addAttribute("allTours", voucherService.findAll());
        model.addAttribute("user", currentUser);
        return "manager-account";
    }

    @PostMapping("/vouchers/{id}/hot")
    public String toggleHotStatus(@PathVariable String id) {
        VoucherDTO voucher = voucherService.findById(id);
        voucher.setIsHot(!voucher.getIsHot());
        voucherService.changeHotStatus(id, voucher);
        return "redirect:/manager/account";
    }

    @PostMapping("/vouchers/{id}/status")
    public String updateVoucherStatus(@PathVariable String id, @RequestParam("status") String status) {
        voucherService.changeVoucherStatus(id, status);
        return "redirect:/manager/account";
    }
}
