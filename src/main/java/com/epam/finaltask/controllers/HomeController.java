package com.epam.finaltask.controllers;

import com.epam.finaltask.dto.VoucherDTO;
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
                           @RequestParam(value = "keyword", required = false) String keyword) {

        Pageable pageable = PageRequest.of(page - 1, size);
        List<VoucherDTO> allTours = voucherService.findAll();

        if (keyword != null && !keyword.isEmpty()) {
            allTours = allTours.stream()
                    .filter(tour -> tour.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allTours.size());
        Page<VoucherDTO> tourPage = new PageImpl<>(allTours.subList(start, end), pageable, allTours.size());

        model.addAttribute("tourPage", tourPage);
        model.addAttribute("keyword", keyword);

        int totalPages = tourPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "index";
    }
}