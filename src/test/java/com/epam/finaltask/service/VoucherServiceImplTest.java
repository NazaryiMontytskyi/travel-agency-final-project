package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VoucherServiceImplTest {

    @Mock private VoucherRepository voucherRepository;
    @Mock private UserRepository userRepository;
    @Mock private VoucherMapper voucherMapper;

    @InjectMocks
    private VoucherServiceImpl voucherService;

    private Voucher voucher;
    private VoucherDTO dto;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user1");
        user.setVouchers(new ArrayList<>());

        voucher = new Voucher();
        voucher.setId(UUID.randomUUID());
        voucher.setTitle("Trip");
        voucher.setUser(null);

        dto = new VoucherDTO();
        dto.setId(voucher.getId().toString());
        dto.setTitle("Trip");
    }

    @Test
    void create_ShouldSaveVoucherAndReturnDTO() {
        when(voucherMapper.toVoucher(dto)).thenReturn(voucher);
        when(voucherRepository.save(any())).thenReturn(voucher);
        when(voucherMapper.toVoucherDTO(any())).thenReturn(dto);

        VoucherDTO result = voucherService.create(dto);

        assertNotNull(result);
        verify(voucherRepository).save(any(Voucher.class));
    }

    @Test
    void order_ShouldAssignVoucherToUser() {
        when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(voucherMapper.toVoucherDTO(any())).thenReturn(dto);

        VoucherDTO result = voucherService.order(voucher.getId().toString(), user.getId().toString());

        assertEquals("Trip", result.getTitle());
        assertEquals(user, voucher.getUser());
        verify(voucherRepository).save(voucher);
        verify(userRepository).save(user);
    }

    @Test
    void order_ShouldThrow_WhenVoucherAlreadyHasUser() {
        voucher.setUser(new User());
        when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));

        assertThrows(IllegalStateException.class,
                () -> voucherService.order(voucher.getId().toString(), user.getId().toString()));
    }

    @Test
    void findById_ShouldReturnDTO_WhenExists() {
        when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));
        when(voucherMapper.toVoucherDTO(any())).thenReturn(dto);

        VoucherDTO result = voucherService.findById(voucher.getId().toString());

        assertEquals("Trip", result.getTitle());
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(voucherRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> voucherService.findById(voucher.getId().toString()));
    }
}
