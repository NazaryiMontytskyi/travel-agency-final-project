package com.epam.finaltask.mapper.impl;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VoucherMapperImpl implements VoucherMapper {
    @Override
    public Voucher toVoucher(VoucherDTO voucherDTO) {
        return Voucher.builder()
                .id(voucherDTO.getId() != null ? UUID.fromString(voucherDTO.getId()) : null)
                .title(voucherDTO.getTitle())
                .description(voucherDTO.getDescription())
                .price(voucherDTO.getPrice())
                .tourType(TourType.valueOf(voucherDTO.getTourType()))
                .transferType(TransferType.valueOf(voucherDTO.getTransferType()))
                .hotelType(HotelType.valueOf(voucherDTO.getHotelType()))
                .status(VoucherStatus.valueOf(voucherDTO.getStatus()))
                .arrivalDate(voucherDTO.getArrivalDate())
                .evictionDate(voucherDTO.getEvictionDate())
                .user(voucherDTO.getUserId() != null ? User.builder().id(voucherDTO.getUserId()).build() : null)
                .isHot(voucherDTO.getIsHot())
                .build();

    }

    @Override
    public VoucherDTO toVoucherDTO(Voucher voucher) {
        return VoucherDTO.builder()
                .id(voucher.getId().toString())
                .title(voucher.getTitle())
                .description(voucher.getDescription())
                .price(voucher.getPrice())
                .tourType(voucher.getTourType().toString())
                .transferType(voucher.getTransferType().toString())
                .hotelType(voucher.getHotelType().toString())
                .status(voucher.getStatus().toString())
                .arrivalDate(voucher.getArrivalDate())
                .evictionDate(voucher.getEvictionDate())
                .userId(voucher.getUser() != null ? voucher.getUser().getId() : null)
                .isHot(voucher.isHot())
                .build();

    }
}
