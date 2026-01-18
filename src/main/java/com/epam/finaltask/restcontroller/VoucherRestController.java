package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.StatusUpdateRequest;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.dto.VoucherSearchParameters;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Voucher Management", description = "APIs for managing vouchers")
public class VoucherRestController {

    private final VoucherService voucherService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('user:read') or hasAuthority('admin:read')")
    @Operation(summary = "Find all vouchers for a user", description = "Returns a list of all vouchers belonging to a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved vouchers"),
            @ApiResponse(responseCode = "204", description = "No vouchers found for this user", content = @Content)
    })
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
    @Operation(summary = "Create a new voucher", description = "Creates a new voucher with the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voucher created successfully"),
            @ApiResponse(responseCode = "204", description = "Request body is empty", content = @Content)
    })
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
    @Operation(summary = "Change a voucher's 'hot' status", description = "Sets or unsets the 'hot' status for a voucher.")
    @ApiResponse(responseCode = "200", description = "Voucher status changed successfully")
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
    @Operation(summary = "Find all vouchers", description = "Returns a list of all available vouchers.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved vouchers")
    public ResponseEntity<Map<String, Object>> findAll(){
        Map<String, Object> response = new HashMap<>();
        List<VoucherDTO> vouchers = this.voucherService.findAll();
        response.put("results", vouchers);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{voucherId}")
    @PreAuthorize("hasAuthority('admin:update')")
    @Operation(summary = "Update a voucher", description = "Updates the details of an existing voucher.")
    @ApiResponse(responseCode = "200", description = "Voucher updated successfully")
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
    @Operation(summary = "Delete a voucher", description = "Deletes a voucher by its ID.")
    @ApiResponse(responseCode = "200", description = "Voucher deleted successfully")
    public ResponseEntity<Map<String, Object>> deleteVoucher(@PathVariable String voucherId){
        Map<String, Object> response = new HashMap<>();
        this.voucherService.delete(voucherId);
        response.put("statusCode", "OK");
        response.put("statusMessage", String.format("Voucher with Id %s has been deleted", voucherId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/user/{userId}")
    @PreAuthorize("hasAuthority('user:update')")
    @Operation(summary = "Order a voucher", description = "Assigns a voucher to a user.")
    @ApiResponse(responseCode = "200", description = "Voucher ordered successfully")
    public ResponseEntity<VoucherDTO> orderVoucher(@PathVariable String id, @PathVariable String userId){
        var ordered = this.voucherService.order(id, userId);
        return ResponseEntity.ok(ordered);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read') or hasAuthority('admin:read')")
    @Operation(summary = "Find a voucher by ID", description = "Returns a single voucher by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved voucher"),
            @ApiResponse(responseCode = "404", description = "Voucher not found", content = @Content)
    })
    public ResponseEntity<VoucherDTO> findById(@PathVariable String id){
        var foundVoucher = this.voucherService.findById(id);
        if(foundVoucher == null){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundVoucher);
    }

    @GetMapping("/tourType/{tourType}")
    @PreAuthorize("hasAuthority('user:read') or hasAuthority('admin:read')")
    @Operation(summary = "Find vouchers by tour type", description = "Returns a list of vouchers matching the specified tour type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved vouchers"),
            @ApiResponse(responseCode = "400", description = "Invalid tour type", content = @Content)
    })
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
    @Operation(summary = "Find vouchers by transfer type", description = "Returns a list of vouchers matching the specified transfer type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved vouchers"),
            @ApiResponse(responseCode = "400", description = "Invalid transfer type", content = @Content)
    })
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
    @Operation(summary = "Find vouchers by price", description = "Returns a list of vouchers with a price less than or equal to the specified value.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved vouchers"),
            @ApiResponse(responseCode = "400", description = "Invalid price value", content = @Content)
    })
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
    @Operation(summary = "Find vouchers by hotel type", description = "Returns a list of vouchers matching the specified hotel type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved vouchers"),
            @ApiResponse(responseCode = "400", description = "Invalid hotel type", content = @Content)
    })
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
    @Operation(summary = "Change voucher status", description = "Changes the status of an existing voucher.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voucher status changed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<VoucherDTO> changeStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest status){
        return Optional.ofNullable(this.voucherService.changeVoucherStatus(id, status.getStatus()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/all")
    @PreAuthorize("true")
    @Operation(summary = "Find all vouchers by parameters", description = "Returns a paginated list of vouchers matching the specified search parameters.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved vouchers")
    public ResponseEntity<List<VoucherDTO>> findAllByParams(Pageable pageable, @RequestBody VoucherSearchParameters params){
        return ResponseEntity.ok(this.voucherService.findAllByParameters(pageable, params));
    }

}
