package com.epam.finaltask.controllers;

import com.epam.finaltask.auth.CustomUserDetailsService;
import com.epam.finaltask.config.SecurityConfig;
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
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TourController.class)
@Import(SecurityConfig.class)
class TourControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoucherService voucherService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder encoder;

    private VoucherDTO testVoucher;
    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        String voucherId = UUID.randomUUID().toString();
        testVoucher = VoucherDTO.builder().id(voucherId).price(500.0).build();
        testUser = UserDTO.builder().id(UUID.randomUUID().toString()).username("testuser").build();
    }

    @Test
    void watchTour_ShouldReturnTourDetailsView() throws Exception {
        when(voucherService.findById(testVoucher.getId())).thenReturn(testVoucher);

        mockMvc.perform(get("/tours/{id}", testVoucher.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("tour-details"))
                .andExpect(model().attribute("tour", testVoucher));
    }

    @Test
    @WithMockUser
    void orderTourForm_ShouldReturnOrderConfirmationView() throws Exception {
        when(voucherService.findById(testVoucher.getId())).thenReturn(testVoucher);

        mockMvc.perform(get("/order/{id}", testVoucher.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("order-confirmation"))
                .andExpect(model().attribute("tour", testVoucher));
    }

    @Test
    @WithMockUser(username = "testuser")
    void processOrder_ShouldRedirectToPayment_WhenFundsAreSufficient() throws Exception {
        testUser.setBalance(1000.0); // Достатньо коштів
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(voucherService.findById(testVoucher.getId())).thenReturn(testVoucher);

        mockMvc.perform(post("/order/{id}", testVoucher.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/" + testVoucher.getId()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void processOrder_ShouldRedirectToOrderForm_WhenFundsAreInsufficient() throws Exception {
        testUser.setBalance(100.0); // Недостатньо коштів
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(voucherService.findById(testVoucher.getId())).thenReturn(testVoucher);

        mockMvc.perform(post("/order/{id}", testVoucher.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order/" + testVoucher.getId()))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}