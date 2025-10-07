package com.epam.finaltask.dto;

import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.Voucher;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class VoucherSpecifications {

    public static Specification<Voucher> hasHotelType(String hotelType) {
        return (root, query, cb) -> {
            if (hotelType == null || hotelType.isEmpty()) return cb.conjunction();
            return cb.equal(root.get("hotelType"), HotelType.valueOf(hotelType));
        };
    }

    public static Specification<Voucher> hasTourType(String tourType) {
        return (root, query, cb) -> {
            if (tourType == null || tourType.isEmpty()) return cb.conjunction();
            return cb.equal(root.get("tourType"), TourType.valueOf(tourType));
        };
    }

    public static Specification<Voucher> hasTransferType(String transferType) {
        return (root, query, cb) -> {
            if (transferType == null || transferType.isEmpty()) return cb.conjunction();
            return cb.equal(root.get("transferType"), TransferType.valueOf(transferType));
        };
    }

    public static Specification<Voucher> isHot(Boolean isHot) {
        return (root, query, cb) -> {
            if (isHot == null) return cb.conjunction();
            return cb.equal(root.get("isHot"), isHot);
        };
    }

    public static Specification<Voucher> hasPriceGreaterThanOrEqual(BigDecimal priceMin) {
        return (root, query, cb) -> {
            if (priceMin == null) return null;
            return cb.greaterThanOrEqualTo(root.get("price"), priceMin);
        };
    }

    public static Specification<Voucher> hasPriceLessThanOrEqual(BigDecimal priceMax) {
        return (root, query, cb) -> {
            if (priceMax == null) return null;
            return cb.lessThanOrEqualTo(root.get("price"), priceMax);
        };
    }
}
