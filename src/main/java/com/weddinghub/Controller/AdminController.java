package com.weddinghub.Controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weddinghub.dto.ApiDtos.PaymentDecision;
import com.weddinghub.dto.ApiDtos.PaymentResponse;
import com.weddinghub.dto.ApiDtos.VendorDecision;
import com.weddinghub.dto.ApiDtos.VendorResponse;
import com.weddinghub.dto.ApiDtos.BookingResponse;
import com.weddinghub.dto.ApiDtos.BookingStatusRequest;
import com.weddinghub.dto.ApiDtos.CateringResponse;
import com.weddinghub.dto.ApiDtos.DecorationResponse;
import com.weddinghub.dto.ApiDtos.HallResponse;
import com.weddinghub.dto.ApiDtos.HallStatusRequest;
import com.weddinghub.dto.ApiDtos.PackageStatusRequest;
import com.weddinghub.dto.ApiDtos.UserResponse;
import com.weddinghub.entity.VendorApprovalStatus;
import com.weddinghub.entity.UserRole;
import com.weddinghub.service.AdminService;

@Validated
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {

        this.adminService = adminService;
    }

    @GetMapping("/vendors")
    public List<VendorResponse> vendors(@RequestParam(required = false) VendorApprovalStatus status) {
        return adminService.vendors(status);
    }

    @GetMapping("/users")
    public List<UserResponse> users(@RequestParam(required = false) UserRole role) {

        return adminService.users(role);
    }

    @PatchMapping("/vendors/{id}/approval")
    public VendorResponse decideVendor(@PathVariable @Positive Long id, @Valid @RequestBody VendorDecision decision) {
        return adminService.decideVendor(id, decision);
    }

    @PatchMapping("/payments/{id}")
    public PaymentResponse decidePayment(@PathVariable @Positive Long id, @Valid @RequestBody PaymentDecision decision) {
        return adminService.decidePayment(id, decision);
    }

    @GetMapping("/halls")
    public List<HallResponse> halls() {
        return adminService.halls();
    }

    @PatchMapping("/halls/{id}/status")
    public HallResponse hallStatus(@PathVariable @Positive Long id, @Valid @RequestBody HallStatusRequest request) {
        return adminService.hallStatus(id, request);
    }

    @GetMapping("/catering-packages")
    public List<CateringResponse> catering() {
        return adminService.catering();
    }

    @PatchMapping("/catering-packages/{id}/status")
    public CateringResponse cateringStatus(@PathVariable @Positive Long id,
                                           @Valid @RequestBody PackageStatusRequest request) {
        return adminService.cateringStatus(id, request);
    }

    @GetMapping("/decoration-packages")
    public List<DecorationResponse> decorations() {
        return adminService.decorations();
    }

    @PatchMapping("/decoration-packages/{id}/status")
    public DecorationResponse decorationStatus(@PathVariable @Positive Long id,
                                               @Valid @RequestBody PackageStatusRequest request) {
        return adminService.decorationStatus(id, request);
    }

    @GetMapping("/bookings")
    public List<BookingResponse> bookings() {
        return adminService.bookings();
    }

    @PatchMapping("/bookings/{id}/status")
    public BookingResponse bookingStatus(@PathVariable @Positive Long id,
                                         @Valid @RequestBody BookingStatusRequest request) {
        return adminService.bookingStatus(id, request);
    }
}
