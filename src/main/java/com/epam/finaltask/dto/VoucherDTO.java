package com.epam.finaltask.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDTO {

    private String id;

    @NotNull
    @NotBlank(message = "Title can't be blank")
    private String title;

    private String description;

    @NotNull
    private Double price;

    @NotNull
    private String tourType;

    @NotNull
    private String transferType;

    @NotNull
    private String hotelType;

    @NotNull
    private String status;

    @NotNull(message = "Arrival date must be present")
    @FutureOrPresent(message = "Arrival date can't be in the past")
    private LocalDate arrivalDate;

    @NotNull
    @FutureOrPresent(message = "Eviction date can't be in the past")
    private LocalDate evictionDate;

    private UUID userId;

    @NotNull
    private Boolean isHot;
    
}
