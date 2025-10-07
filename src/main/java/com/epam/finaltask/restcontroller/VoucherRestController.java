package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.StatusUpdateRequest;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.dto.VoucherSearchParameters;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherRestController {

    private final VoucherService voucherService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('user:read') or hasAuthority('admin:read')")
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
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<Map<String, Object>> createVoucher(@Valid @RequestBody VoucherDTO voucherDTO) {
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
    @PreAuthorize("hasAuthority('manager:update') or hasAuthority('admin:update')")
    public ResponseEntity<Map<String, Object>> changeHotStatus(@PathVariable String voucherId, @Valid @RequestBody VoucherDTO voucherDTO) {
        Map<String, Object> response = new HashMap<>();
        var changedStatus = this.voucherService.changeHotStatus(voucherId, voucherDTO);
        response.put("voucher", changedStatus);
        response.put("statusCode", "OK");
        response.put("statusMessage", "Voucher status is successfully changed");
        return ResponseEntity.ok(response);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('user:read') or hasAuthority('admin:read')")
    public ResponseEntity<Map<String, Object>> findAll(){
        Map<String, Object> response = new HashMap<>();
        List<VoucherDTO> vouchers = this.voucherService.findAll();
        response.put("results", vouchers);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{voucherId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<Map<String, Object>> updateVoucher(@PathVariable String voucherId, @Valid @RequestBody VoucherDTO voucherDTO) {
        Map<String, Object> response = new HashMap<>();
        var updatedValue = this.voucherService.update(voucherId, voucherDTO);
        response.put("statusCode", "OK");
        response.put("statusMessage", "Voucher is successfully updated");
        response.put("voucher", updatedValue);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{voucherId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<Map<String, Object>> deleteVoucher(@PathVariable String voucherId){
        Map<String, Object> response = new HashMap<>();
        this.voucherService.delete(voucherId);
        response.put("statusCode", "OK");
        response.put("statusMessage", String.format("Voucher with Id %s has been deleted", voucherId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/user/{userId}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<VoucherDTO> orderVoucher(@PathVariable String id, @PathVariable String userId){
        var ordered = this.voucherService.order(id, userId);
        return ResponseEntity.ok(ordered);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read') or hasAuthority('admin:read')")
    public ResponseEntity<VoucherDTO> findById(@PathVariable String id){
        var foundVoucher = this.voucherService.findById(id);
        if(foundVoucher == null){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundVoucher);
    }

    @GetMapping("/tourType/{tourType}")
    @PreAuthorize("hasAuthority('user:read') or hasAuthority('admin:read')")
    public ResponseEntity<List<VoucherDTO>> findAllByTourType(@PathVariable String tourType){
        TourType targetType;
        try{
            targetType = TourType.valueOf(tourType);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }

        var vouchersWithType = this.voucherService.findAllByTourType(targetType);
        return ResponseEntity.ok(vouchersWithType);
    }


    @GetMapping("/transferType/{transferType}")
    @PreAuthorize("hasAuthority('user:read') or hasAuthority('admin:read')")
    public ResponseEntity<List<VoucherDTO>> findAllByTransferType(@PathVariable String transferType){
        TransferType targetType;
        try{
            targetType = TransferType.valueOf(transferType);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(this.voucherService.findAllByTransferType(targetType.name()));
    }

    @GetMapping("/price/{price}")
    @PreAuthorize("hasAuthority('user:read') or hasAuthority('admin:read')")
    public ResponseEntity<List<VoucherDTO>> findAllByPrice(@PathVariable String price){
        var doublePrice = Double.parseDouble(price);
        if(doublePrice < 0.0){
            return ResponseEntity.badRequest().build();
        }
        var foundVouchersByPrice = this.voucherService.findAllByPrice(doublePrice);
        return ResponseEntity.ok(foundVouchersByPrice);
    }

    @GetMapping("/hotelType/{hotelType}")
    @PreAuthorize("hasAuthority('user:read') or hasAuthority('admin:read')")
    public ResponseEntity<List<VoucherDTO>> findAllByHotel(@PathVariable String hotelType){
        HotelType targetType;
        try{
            targetType = HotelType.valueOf(hotelType);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        var foundVouchers = this.voucherService.findAllByHotelType(targetType);
        return ResponseEntity.ok(foundVouchers);
    }

    @PatchMapping("/status/{id}")
    @PreAuthorize("hasAuthority('manager:update') or hasAuthority('admin:update')")
    public ResponseEntity<VoucherDTO> changeStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest status){
        return Optional.ofNullable(this.voucherService.changeVoucherStatus(id, status.getStatus()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/all")
    @PreAuthorize("true")
    public ResponseEntity<List<VoucherDTO>> findAllByParams(Pageable pageable, @RequestBody VoucherSearchParameters params){
        return ResponseEntity.ok(this.voucherService.findAllByParameters(pageable, params));
    }

}
