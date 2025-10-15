package com.epam.finaltask.dto;

import com.epam.finaltask.model.VoucherStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
    @Size(min = 5, max = 30, message = "Title can't be shorter than 5 and longer than 30 characters")
    private String title;

    @Size(min = 10, max = 300, message = "Description is limited from 10 to 300 characters")
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
    private String status = VoucherStatus.REGISTERED.name();

    @NotNull(message = "Arrival date must be present")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate arrivalDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate evictionDate;

    private UUID userId;

    @NotNull
    private Boolean isHot;
    
}
