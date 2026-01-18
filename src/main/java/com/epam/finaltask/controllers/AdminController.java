package com.epam.finaltask.controllers;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.*;
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
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('admin:read', 'admin:update')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final VoucherService voucherService;

    @GetMapping("/panel")
    public String adminPanel(Model model, Principal principal) {
        var currentUser = this.userService.getUserByUsername(principal.getName());
        model.addAttribute("allUsers", userService.findAll());
        model.addAttribute("allTours", voucherService.findAll());
        model.addAttribute("allRoles", Role.values());
        model.addAttribute("user", currentUser);
        return "admin-panel";
    }

    @PostMapping("/users/{id}/block")
    public String blockUser(@PathVariable String id) {
        userService.blockUser(id);
        return "redirect:/admin/panel";
    }

    @PostMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable String id) {
        userService.unblockUser(id);
        return "redirect:/admin/panel";
    }

    @GetMapping("/vouchers/{id}/edit")
    public String showEditVoucherForm(@PathVariable String id, Model model) {
        VoucherDTO voucher = voucherService.findById(id);
        if (voucher.getUserId() != null) {
            return "redirect:/admin/panel?error=Voucher is already booked";
        }
        model.addAttribute("voucher", voucher);
        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("transferTypes", TransferType.values());
        model.addAttribute("hotelTypes", HotelType.values());
        return "edit-voucher";
    }

    @PostMapping("/vouchers/{id}/edit")
    public String updateVoucher(@PathVariable String id,
                                @Valid @ModelAttribute("voucher") VoucherDTO voucherDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tourTypes", TourType.values());
            model.addAttribute("transferTypes", TransferType.values());
            model.addAttribute("hotelTypes", HotelType.values());
            return "edit-voucher";
        }

        voucherService.update(id, voucherDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Voucher updated successfully!");
        return "redirect:/admin/panel";
    }

    @PostMapping("/vouchers/{id}/delete")
    @PreAuthorize("hasAuthority('admin:delete')")
    public String deleteVoucher(@PathVariable String id, RedirectAttributes redirectAttributes) {
        voucherService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Voucher deleted successfully!");
        return "redirect:/admin/panel";
    }

    @GetMapping("/vouchers/new")
    @PreAuthorize("hasAuthority('admin:create')")
    public String showCreateVoucherForm(Model model) {
        VoucherDTO voucher = new VoucherDTO();
        voucher.setIsHot(false);
        model.addAttribute("voucher", voucher);
        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("transferTypes", TransferType.values());
        model.addAttribute("hotelTypes", HotelType.values());
        return "create-voucher";
    }

    @PostMapping("/vouchers/new")
    @PreAuthorize("hasAuthority('admin:create')")
    public String createVoucher(@Valid @ModelAttribute("voucher") VoucherDTO voucherDTO,
                                BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tourTypes", TourType.values());
            model.addAttribute("transferTypes", TransferType.values());
            model.addAttribute("hotelTypes", HotelType.values());
            return "create-voucher";
        }
        voucherDTO.setStatus(VoucherStatus.REGISTERED.name());
        voucherDTO.setIsHot(false);
        voucherService.create(voucherDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Voucher created successfully!");
        return "redirect:/admin/panel";
    }

    @PostMapping("/users/{id}/change-role")
    @PreAuthorize("hasAuthority('admin:update')")
    public String changeUserRole(@PathVariable String id, @RequestParam("role") String role, RedirectAttributes redirectAttributes) {
        userService.changeUserRole(id, role);
        redirectAttributes.addFlashAttribute("successMessage", "User role updated successfully!");
        return "redirect:/admin/panel";
    }
}