package com.epam.finaltask.controllers;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.dto.VoucherSearchParameters;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final VoucherService voucherService;

    @GetMapping("/")
    public String homePage(Model model,
                           @RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "size", defaultValue = "6") int size,
                           VoucherSearchParameters searchParams) {

        Pageable pageable = PageRequest.of(page - 1, size);
        List<VoucherDTO> filteredTours = voucherService.findAllByParameters(pageable, searchParams);

        Page<VoucherDTO> tourPage = new PageImpl<>(filteredTours, pageable, voucherService.findAll().size());

        model.addAttribute("tourPage", tourPage);
        model.addAttribute("searchParams", searchParams);

        model.addAttribute("hotelTypes", HotelType.values());
        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("transferTypes", TransferType.values());

        int totalPages = tourPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "index";
    }
}