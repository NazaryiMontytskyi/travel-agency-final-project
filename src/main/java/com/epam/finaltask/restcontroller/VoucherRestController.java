package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherRestController {

    private final VoucherService voucherService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> findAllVouchers(@PathVariable String userId) {
        var resultingVouchers = voucherService.findAllByUserId(userId);
        if(resultingVouchers == null){
            return ResponseEntity.noContent().build();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("results", resultingVouchers);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createVoucher(@RequestBody VoucherDTO voucherDTO) {
        if(voucherDTO == null){
            return ResponseEntity.noContent().build();
        }
        var created = this.voucherService.create(voucherDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", "OK");
        response.put("statusMessage", "Voucher is successfully created");
        response.put("voucher", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{voucherId}/status")
    public ResponseEntity<Map<String, Object>> changeHotStatus(@PathVariable String voucherId, @RequestBody VoucherDTO voucherDTO) {
        Map<String, Object> response = new HashMap<>();
        var changedStatus = this.voucherService.changeHotStatus(voucherId, voucherDTO);
        response.put("voucher", changedStatus);
        response.put("statusCode", "OK");
        response.put("statusMessage", "Voucher status is successfully changed");
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(){
        Map<String, Object> response = new HashMap<>();
        List<VoucherDTO> vouchers = this.voucherService.findAll();
        response.put("results", vouchers);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{voucherId}")
    public ResponseEntity<Map<String, Object>> updateVoucher(@PathVariable String voucherId, @RequestBody VoucherDTO voucherDTO) {
        Map<String, Object> response = new HashMap<>();
        var updatedValue = this.voucherService.update(voucherId, voucherDTO);
        response.put("statusCode", "OK");
        response.put("statusMessage", "Voucher is successfully updated");
        response.put("voucher", updatedValue);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{voucherId}")
    public ResponseEntity<Map<String, Object>> updateVoucher(@PathVariable String voucherId){
        Map<String, Object> response = new HashMap<>();
        this.voucherService.delete(voucherId);
        response.put("statusCode", "OK");
        response.put("statusMessage", String.format("Voucher with Id %s has been deleted", voucherId));
        return ResponseEntity.ok(response);
    }

}
