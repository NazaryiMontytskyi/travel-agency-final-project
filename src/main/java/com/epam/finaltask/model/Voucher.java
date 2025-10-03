package com.epam.finaltask.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String description;

    private Double price;

    private TourType tourType;

    private TransferType transferType;

    private HotelType hotelType;

    private VoucherStatus status;

    private LocalDate arrivalDate;

    private LocalDate evictionDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isHot;


}
