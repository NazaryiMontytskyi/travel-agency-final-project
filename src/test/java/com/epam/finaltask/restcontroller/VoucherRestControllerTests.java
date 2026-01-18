package com.epam.finaltask.restcontroller;

import com.epam.finaltask.auth.CustomUserDetailsService;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.service.VoucherService;
import com.epam.finaltask.token.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VoucherRestController.class)
@EnableMethodSecurity
public class VoucherRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VoucherService voucherService;

    // Мокаємо сервіси, які є залежностями для Spring Security
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtService jwtService;

    private VoucherDTO createTestVoucherDTO(String id) {
        return VoucherDTO.builder()
                .id(id)
                .title("Amazing Trip to Paris")
                .description("A wonderful journey")
                .price(1500.00)
                .tourType(TourType.ADVENTURE.name())
                .transferType(TransferType.PLANE.name())
                .hotelType(HotelType.FIVE_STARS.name())
                .status("AVAILABLE")
                .arrivalDate(LocalDate.now().plusDays(30))
                .evictionDate(LocalDate.now().plusDays(37))
                .isHot(false)
                .build();
    }

    @Nested
    @DisplayName("Tests for findAllVouchers (GET /api/vouchers/user/{userId})")
    class FindAllVouchersByUser {

        @Test
        @DisplayName("Should return vouchers for a specific user")
        @WithMockUser(authorities = "admin:read")
        void shouldReturnVouchersForUser() throws Exception {
            VoucherDTO dto = createTestVoucherDTO("voucher1");
            when(voucherService.findAllByUserId("user1")).thenReturn(List.of(dto));

            mockMvc.perform(get("/api/vouchers/user/{userId}", "user1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.results").isArray())
                    .andExpect(jsonPath("$.results.length()").value(1))
                    .andExpect(jsonPath("$.results[0].id").value("voucher1"));
        }

        @Test
        @DisplayName("Should return 204 No Content when service returns null")
        @WithMockUser(authorities = "user:read")
        void shouldReturnNoContentWhenNoVouchersFound() throws Exception {
            when(voucherService.findAllByUserId("user1")).thenReturn(null);

            mockMvc.perform(get("/api/vouchers/user/{userId}", "user1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 403 Forbidden for user with insufficient permissions")
        @WithMockUser(authorities = "some:other_permission")
        void shouldReturnForbiddenForInsufficientPermissions() throws Exception {
            mockMvc.perform(get("/api/vouchers/user/{userId}", "user1"))
                    .andExpect(status().isForbidden());
        }
    }


    @Nested
    @DisplayName("Tests for createVoucher (POST /api/vouchers)")
    class CreateVoucher {
        @Test
        @DisplayName("Should create a voucher successfully")
        @WithMockUser(authorities = "admin:create")
        void shouldCreateVoucher() throws Exception {
            VoucherDTO requestDTO = createTestVoucherDTO(null); // ID генерується сервісом
            VoucherDTO responseDTO = createTestVoucherDTO("newVoucherId");
            when(voucherService.create(any(VoucherDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/api/vouchers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.statusCode").value("OK"))
                    .andExpect(jsonPath("$.statusMessage").value("Voucher is successfully created"))
                    .andExpect(jsonPath("$.voucher.id").value("newVoucherId"));
        }
    }

    @Nested
    @DisplayName("Tests for changeHotStatus (PATCH /{voucherId}/status)")
    class ChangeHotStatus {
        @Test
        @DisplayName("Should change hot status successfully")
        @WithMockUser(authorities = "manager:update")
        void shouldChangeHotStatus() throws Exception {
            VoucherDTO requestDTO = createTestVoucherDTO("voucher123");

            requestDTO.setIsHot(true);

            VoucherDTO responseDTO = createTestVoucherDTO("voucher123");
            responseDTO.setIsHot(true);

            when(voucherService.changeHotStatus(eq("voucher123"), any(VoucherDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(patch("/api/vouchers/{voucherId}/status", "voucher123")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.voucher.id").value("voucher123"))
                    .andExpect(jsonPath("$.voucher.isHot").value(true));
        }
    }

    @Nested
    @DisplayName("Tests for findAll (GET /api/vouchers)")
    class FindAll {
        @Test
        @DisplayName("Should return all vouchers")
        @WithMockUser(authorities = "user:read")
        void shouldFindAllVouchers() throws Exception {
            VoucherDTO dto1 = createTestVoucherDTO("voucher1");
            VoucherDTO dto2 = createTestVoucherDTO("voucher2");
            when(voucherService.findAll()).thenReturn(List.of(dto1, dto2));

            mockMvc.perform(get("/api/vouchers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.results").isArray())
                    .andExpect(jsonPath("$.results.length()").value(2));
        }
    }

    @Nested
    @DisplayName("Tests for updateVoucher (PATCH /{voucherId})")
    class UpdateVoucher {
        @Test
        @DisplayName("Should update a voucher successfully")
        @WithMockUser(authorities = "admin:update")
        void shouldUpdateVoucher() throws Exception {
            VoucherDTO requestDTO = createTestVoucherDTO("voucher123");
            requestDTO.setPrice(2000.00); // Updated price
            when(voucherService.update(eq("voucher123"), any(VoucherDTO.class))).thenReturn(requestDTO);

            mockMvc.perform(patch("/api/vouchers/{voucherId}", "voucher123")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.voucher.price").value(2000.00));
        }
    }

    @Nested
    @DisplayName("Tests for deleteVoucher (DELETE /{voucherId})")
    class DeleteVoucher {
        @Test
        @DisplayName("Should delete a voucher successfully")
        @WithMockUser(authorities = "admin:delete")
        void shouldDeleteVoucher() throws Exception {
            String voucherId = "voucherToDelete";
            doNothing().when(voucherService).delete(voucherId);

            mockMvc.perform(delete("/api/vouchers/{voucherId}", voucherId).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusCode").value("OK"))
                    .andExpect(jsonPath("$.statusMessage").value("Voucher with Id " + voucherId + " has been deleted"));
        }
    }

    @Nested
    @DisplayName("Tests for orderVoucher (POST /{id}/user/{userId})")
    class OrderVoucher {
        @Test
        @DisplayName("Should order a voucher for a user successfully")
        @WithMockUser(authorities = "user:update")
        void shouldOrderVoucher() throws Exception {
            VoucherDTO orderedVoucher = createTestVoucherDTO("voucher123");
            when(voucherService.order("voucher123", "user1")).thenReturn(orderedVoucher);

            mockMvc.perform(post("/api/vouchers/{id}/user/{userId}", "voucher123", "user1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("voucher123"));
        }
    }

    @Nested
    @DisplayName("Tests for findById (GET /{id})")
    class FindById {
        @Test
        @DisplayName("Should return a voucher by its ID")
        @WithMockUser(authorities = "user:read")
        void shouldFindById() throws Exception {
            VoucherDTO voucher = createTestVoucherDTO("voucher123");
            when(voucherService.findById("voucher123")).thenReturn(voucher);

            mockMvc.perform(get("/api/vouchers/{id}", "voucher123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("voucher123"))
                    .andExpect(jsonPath("$.title").value(voucher.getTitle()));
        }

        @Test
        @DisplayName("Should return 200 OK with empty body when voucher is not found")
        @WithMockUser(authorities = "user:read")
        void shouldReturnOkWithEmptyBodyWhenNotFound() throws Exception {
            when(voucherService.findById("nonExistentId")).thenReturn(null);
            mockMvc.perform(get("/api/vouchers/{id}", "nonExistentId"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }
    }

    @Nested
    @DisplayName("Tests for filtering methods")
    class FilterMethods {
        @Test
        @DisplayName("Should find vouchers by TourType")
        @WithMockUser(authorities = "user:read")
        void shouldFindAllByTourType() throws Exception {
            VoucherDTO voucher = createTestVoucherDTO("voucher1");
            when(voucherService.findAllByTourType(TourType.ADVENTURE)).thenReturn(List.of(voucher));

            mockMvc.perform(get("/api/vouchers/tourType/{tourType}", "ADVENTURE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("Should return 400 Bad Request for invalid TourType")
        @WithMockUser(authorities = "user:read")
        void shouldReturnBadRequestForInvalidTourType() throws Exception {
            mockMvc.perform(get("/api/vouchers/tourType/{tourType}", "INVALID_TYPE"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 Bad Request for negative price")
        @WithMockUser(authorities = "user:read")
        void shouldReturnBadRequestForNegativePrice() throws Exception {
            mockMvc.perform(get("/api/vouchers/price/{price}", "-100.0"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Tests for changeStatus (PATCH /status/{id})")
    class ChangeStatus {
        @Test
        @DisplayName("Should change voucher status successfully")
        @WithMockUser(username = "admin", authorities = {"admin:update"})
        void shouldChangeStatus() throws Exception {
            VoucherDTO updatedVoucher = createTestVoucherDTO("voucher123");
            updatedVoucher.setStatus("REGISTERED");
            String newStatus = "REGISTERED";

            when(voucherService.changeVoucherStatus("voucher123", newStatus)).thenReturn(updatedVoucher);

            mockMvc.perform(patch("/api/vouchers/status/{id}", "voucher123")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("status", newStatus))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("voucher123"))
                    .andExpect(jsonPath("$.status").value("REGISTERED"));
        }
    }
}