package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.dto.VoucherSearchParameters;
import com.epam.finaltask.dto.VoucherSpecifications;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Transactional
    public VoucherDTO order(String id, String userId) {
        Voucher voucherToOrder = voucherRepository.findById(UUID.fromString(id)).orElseThrow(/*...*/);
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(/*...*/);

        BigDecimal price = BigDecimal.valueOf(voucherToOrder.getPrice());
        if (user.getBalance().compareTo(price) < 0) {
            throw new IllegalStateException("Insufficient funds to order this tour.");
        }
        user.setBalance(user.getBalance().subtract(price));

        voucherToOrder.setUser(user);
        voucherToOrder.setStatus(VoucherStatus.REGISTERED);

        userRepository.save(user);
        return voucherMapper.toVoucherDTO(voucherRepository.save(voucherToOrder));
    }


    @Override
    @Transactional
    public void cancelOrder(String voucherId, String username) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId)).orElseThrow(/*...*/);
        User user = userRepository.findUserByUsername(username).orElseThrow(/*...*/);

        if (voucher.getUser() == null || !voucher.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User is not the owner of this voucher.");
        }

        BigDecimal price = BigDecimal.valueOf(voucher.getPrice());
        user.setBalance(user.getBalance().add(price));

        voucher.setUser(null);
        voucher.setStatus(VoucherStatus.CANCELED);

        userRepository.save(user);
        voucherRepository.save(voucher);
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
    public List<VoucherDTO> findAllByParameters(Pageable pageable, VoucherSearchParameters parameters) {
        Specification<Voucher> spec = Specification.where(null);

        if (parameters.hotelType() != null && !parameters.hotelType().isEmpty()) {
            spec = spec.and(VoucherSpecifications.hasHotelType(parameters.hotelType()));
        }
        if (parameters.tourType() != null && !parameters.tourType().isEmpty()) {
            spec = spec.and(VoucherSpecifications.hasTourType(parameters.tourType()));
        }
        if (parameters.transferType() != null && !parameters.transferType().isEmpty()) {
            spec = spec.and(VoucherSpecifications.hasTransferType(parameters.transferType()));
        }

        if (parameters.priceMin() != null && !parameters.priceMin().isEmpty()) {
            try {
                BigDecimal priceMin = BigDecimal.valueOf(Double.parseDouble(parameters.priceMin()));
                spec = spec.and(VoucherSpecifications.hasPriceGreaterThanOrEqual(priceMin));
            } catch (NumberFormatException e) {
            }
        }

        if (parameters.priceMax() != null && !parameters.priceMax().isEmpty()) {
            try {
                BigDecimal priceMax = BigDecimal.valueOf(Double.parseDouble(parameters.priceMax()));
                spec = spec.and(VoucherSpecifications.hasPriceLessThanOrEqual(priceMax));
            } catch (NumberFormatException e) {
            }
        }

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "isHot") // Сортування за "гарячими" турами
        );

        return voucherRepository.findAll(spec, sortedPageable)
                .getContent()
                .stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAll() {
        var finalCollection = this.voucherRepository.findAll().stream().map(voucherMapper::toVoucherDTO).toList();
        if(finalCollection == null){
            return List.of();
        }
        return finalCollection;
    }

    @Override
    public VoucherDTO changeVoucherStatus(String id, String status) {
        try{
            var targetVoucher = this.voucherRepository.findById(UUID.fromString(id));
            Voucher voucher;
            if(targetVoucher.isPresent()){
                voucher = targetVoucher.get();
                voucher.setStatus(VoucherStatus.valueOf(status));
                this.voucherRepository.save(voucher);
                return voucherMapper.toVoucherDTO(voucher);
            }
            else{
                throw new IllegalArgumentException();
            }
        } catch(IllegalArgumentException e){
            return null;
        }
    }
}
