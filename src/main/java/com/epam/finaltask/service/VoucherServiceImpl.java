package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final VoucherMapper voucherMapper;

    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {
        Voucher voucher = voucherMapper.toVoucher(voucherDTO);
        var saved = voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(saved);
    }

    @Override
    public VoucherDTO order(String id, String userId) {
        var voucherToOrder = this.voucherRepository.findById(UUID.fromString(id)).orElseThrow(() -> new IllegalArgumentException("No voucher found with such an id"));
        var user = this.userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UsernameNotFoundException("No user found with such an username"));
        voucherToOrder.setUser(user);
        user.getVouchers().add(voucherToOrder);

        voucherRepository.save(voucherToOrder);
        userRepository.save(user);
        return voucherMapper.toVoucherDTO(voucherToOrder);
    }

    @Override
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        var voucher = this.voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("No voucher found with such an id"));

        voucher.setTitle(compareAndChoose(voucherDTO.getTitle(), voucher.getTitle()));
        voucher.setDescription(compareAndChoose(voucherDTO.getDescription(), voucher.getDescription()));
        voucher.setPrice(compareAndChoose(voucherDTO.getPrice(), voucher.getPrice()));
        voucher.setArrivalDate(compareAndChoose(voucherDTO.getArrivalDate(), voucher.getArrivalDate()));
        voucher.setEvictionDate(compareAndChoose(voucherDTO.getEvictionDate(), voucher.getEvictionDate()));

        voucher.setTourType(safeEnumValue(voucherDTO.getTourType(), TourType.class));
        voucher.setTransferType(safeEnumValue(voucherDTO.getTransferType(), TransferType.class));
        voucher.setHotelType(safeEnumValue(voucherDTO.getHotelType(), HotelType.class));
        voucher.setStatus(safeEnumValue(voucherDTO.getStatus(), VoucherStatus.class));

        User user = null;
        if (voucherDTO.getUserId() != null) {
            user = this.userRepository.findById(voucherDTO.getUserId()).orElse(null);
        }
        voucher.setUser(user);

        voucher.setHot(voucherDTO.getIsHot());

        this.voucherRepository.save(voucher);

        if (user != null) {
            if (!user.getVouchers().contains(voucher)) {
                user.getVouchers().add(voucher);
                userRepository.save(user);
            }
        }

        return voucherMapper.toVoucherDTO(voucher);
    }

    private <T> T compareAndChoose(T newValue, T oldValue) {
        if(newValue == null){
            return oldValue;
        }
        if(oldValue == null){
            return newValue;
        }
        return newValue.equals(oldValue) ? oldValue : newValue;
    }

    private <E extends Enum<E>> E safeEnumValue(String value, Class<E> enumClass) {
        if (value == null) return null;
        return Enum.valueOf(enumClass, value);
    }



    @Override
    public void delete(String voucherId) {
        if(this.voucherRepository.existsById(UUID.fromString(voucherId))){
            this.voucherRepository.deleteById(UUID.fromString(voucherId));
        }
    }

    @Override
    public VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO) {
        if(voucherRepository.existsById(UUID.fromString(id))){
            var voucher = this.voucherRepository.findById(UUID.fromString(id)).orElseThrow(() -> new IllegalArgumentException("No voucher found with such an id"));
            voucher.setHot(voucherDTO.getIsHot());
            this.voucherRepository.save(voucher);
            return voucherMapper.toVoucherDTO(voucher);
        }
        throw new IllegalArgumentException("No voucher found with such an id");
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        var vouchers = this.voucherRepository.findAllByUserId(UUID.fromString(userId));
        return vouchers.stream().map(voucherMapper::toVoucherDTO).toList();
    }

    @Override
    public VoucherDTO findById(String voucherId) {
        if(this.voucherRepository.findById(UUID.fromString(voucherId)).isEmpty()){
            throw new IllegalArgumentException("No voucher found with such an id");
        }
        return voucherMapper.toVoucherDTO(this.voucherRepository.findById(UUID.fromString(voucherId)).get());
    }

    @Override
    public List<VoucherDTO> findAllByTourType(TourType tourType) {
        var vouchers = this.voucherRepository.findAllByTourType(tourType);
        return vouchers.stream().map(voucherMapper::toVoucherDTO).toList();
    }

    @Override
    public List<VoucherDTO> findAllByTransferType(String transferType) {
        var vouchers = this.voucherRepository.findAllByTransferType(TransferType.valueOf(transferType));
        return vouchers.stream().map(voucherMapper::toVoucherDTO).toList();
    }

    @Override
    public List<VoucherDTO> findAllByPrice(Double price) {
        var vouchers = this.voucherRepository.findAllByPrice(price);
        return vouchers.stream().map(voucherMapper::toVoucherDTO).toList();
    }

    @Override
    public List<VoucherDTO> findAllByHotelType(HotelType hotelType) {
        var vouchers = this.voucherRepository.findAllByHotelType(hotelType);
        return vouchers.stream().map(voucherMapper::toVoucherDTO).toList();
    }

    @Override
    public List<VoucherDTO> findAll() {
        var finalCollection = this.voucherRepository.findAll().stream().map(voucherMapper::toVoucherDTO).toList();
        if(finalCollection == null){
            return List.of();
        }
        return finalCollection;
    }
}
