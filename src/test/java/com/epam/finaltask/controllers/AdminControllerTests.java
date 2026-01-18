package com.epam.finaltask.controllers;

import com.epam.finaltask.auth.CustomUserDetailsService;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import com.epam.finaltask.token.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@WithMockUser(username = "admin", authorities = {"admin:read", "admin:update", "admin:delete", "admin:create"})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private VoucherService voucherService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder encoder;

    private UserDTO adminUser;
    private VoucherDTO testVoucher;

    @BeforeEach
    void setUp() {
        adminUser = UserDTO.builder().username("admin").build();
        testVoucher = VoucherDTO.builder()
                .id(UUID.randomUUID().toString())
                .title("Test Voucher")
                .description("A great trip")
                .price(1200.0)
                .tourType("RECREATION")
                .transferType("BUS")
                .hotelType("APARTMENTS")
                .status("REGISTERED")
                .arrivalDate(LocalDate.now().plusDays(10))
                .evictionDate(LocalDate.now().plusDays(20))
                .isHot(false)
                .build();
    }

    @Test
    void adminPanel_ShouldReturnAdminPanelView() throws Exception {
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(userService.findAll()).thenReturn(Collections.emptyList());
        when(voucherService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/panel"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-panel"))
                .andExpect(model().attributeExists("allUsers", "allTours", "user"));
    }

    @Test
    void blockUser_ShouldRedirectToAdminPanel() throws Exception {
        mockMvc.perform(post("/admin/users/some-id/block").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/panel"));
    }

    @Test
    void unblockUser_ShouldRedirectToAdminPanel() throws Exception {
        mockMvc.perform(post("/admin/users/some-id/unblock").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/panel"));
    }

    @Test
    void showEditVoucherForm_ShouldReturnEditVoucherView_WhenVoucherIsNotBooked() throws Exception {
        testVoucher.setUserId(null); // Ваучер не заброньований
        when(voucherService.findById(any(String.class))).thenReturn(testVoucher);

        mockMvc.perform(get("/admin/vouchers/some-id/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-voucher"))
                .andExpect(model().attribute("voucher", testVoucher));
    }

    @Test
    void showEditVoucherForm_ShouldRedirect_WhenVoucherIsBooked() throws Exception {
        testVoucher.setUserId(UUID.randomUUID()); // Ваучер заброньований
        when(voucherService.findById(any(String.class))).thenReturn(testVoucher);

        mockMvc.perform(get("/admin/vouchers/some-id/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/panel?error=Voucher is already booked"));
    }

    @Test
    void updateVoucher_ShouldRedirectToAdminPanel_WhenDataIsValid() throws Exception {
        mockMvc.perform(post("/admin/vouchers/some-id/edit")
                        .with(csrf())
                        .flashAttr("voucher", testVoucher))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/panel"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void deleteVoucher_ShouldRedirectToAdminPanel() throws Exception {
        mockMvc.perform(post("/admin/vouchers/some-id/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/panel"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void createVoucher_ShouldRedirectToAdminPanel_WhenDataIsValid() throws Exception {
        mockMvc.perform(post("/admin/vouchers/new")
                        .with(csrf())
                        .flashAttr("voucher", testVoucher))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/panel"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void changeUserRole_ShouldRedirectToAdminPanel() throws Exception {
        mockMvc.perform(post("/admin/users/some-id/change-role")
                        .with(csrf())
                        .param("role", "MANAGER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/panel"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}