package com.weddinghub.Controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.weddinghub.dto.ApiDtos.CateringRequest;
import com.weddinghub.dto.ApiDtos.CateringResponse;
import com.weddinghub.dto.ApiDtos.BookingResponse;
import com.weddinghub.dto.ApiDtos.BookingStatusRequest;
import com.weddinghub.dto.ApiDtos.DecorationRequest;
import com.weddinghub.dto.ApiDtos.DecorationResponse;
import com.weddinghub.dto.ApiDtos.HallRequest;
import com.weddinghub.dto.ApiDtos.HallResponse;
import com.weddinghub.dto.ApiDtos.HallStatusRequest;
import com.weddinghub.dto.ApiDtos.PackageStatusRequest;
import com.weddinghub.dto.ApiDtos.VendorResponse;
import com.weddinghub.dto.ApiDtos.VendorProfileRequest;
import com.weddinghub.security.WeddingHubPrincipal;
import com.weddinghub.service.VendorManagementService;
import com.weddinghub.service.BookingService;

@Validated
@RestController
@RequestMapping("/api/v1/vendor")
public class VendorController {
    private final VendorManagementService vendorService;
    private final BookingService bookingService;

    public VendorController(VendorManagementService vendorService, BookingService bookingService) {
        this.vendorService = vendorService;
        this.bookingService = bookingService;
    }

    @GetMapping("/profile")
    public VendorResponse profile(@AuthenticationPrincipal WeddingHubPrincipal principal) {
        return vendorService.profile(principal);
    }

    @PutMapping("/profile")
    public VendorResponse updateProfile(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                        @Valid @RequestBody VendorProfileRequest request) {
        return vendorService.updateProfile(principal, request);
    }

    @GetMapping("/bookings")
    public List<BookingResponse> bookings(@AuthenticationPrincipal WeddingHubPrincipal principal) {
        return bookingService.vendorBookings(principal);
    }

    @PatchMapping("/bookings/{id}/status")
    public BookingResponse setBookingStatus(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                            @PathVariable @Positive Long id,
                                            @Valid @RequestBody BookingStatusRequest request) {
        return bookingService.updateVendorStatus(principal, id, request);
    }

    @GetMapping("/halls")
    public List<HallResponse> halls(@AuthenticationPrincipal WeddingHubPrincipal principal) {
        return vendorService.halls(principal);
    }

    @PostMapping("/halls")
    @ResponseStatus(HttpStatus.CREATED)
    public HallResponse createHall(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                   @Valid @RequestBody HallRequest request) {
        return vendorService.createHall(principal, request);
    }

    @PutMapping("/halls/{id}")
    public HallResponse updateHall(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                   @PathVariable @Positive Long id, @Valid @RequestBody HallRequest request) {
        return vendorService.updateHall(principal, id, request);
    }

    @PatchMapping("/halls/{id}/status")
    public HallResponse setHallStatus(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                      @PathVariable @Positive Long id, @Valid @RequestBody HallStatusRequest request) {
        return vendorService.setHallStatus(principal, id, request);
    }

    @GetMapping("/catering-packages")
    public List<CateringResponse> catering(@AuthenticationPrincipal WeddingHubPrincipal principal) {
        return vendorService.catering(principal);
    }

    @PostMapping("/catering-packages")
    @ResponseStatus(HttpStatus.CREATED)
    public CateringResponse createCatering(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                           @Valid @RequestBody CateringRequest request) {
        return vendorService.createCatering(principal, request);
    }

    @PutMapping("/catering-packages/{id}")
    public CateringResponse updateCatering(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                           @PathVariable @Positive Long id, @Valid @RequestBody CateringRequest request) {
        return vendorService.updateCatering(principal, id, request);
    }

    @PatchMapping("/catering-packages/{id}/status")
    public CateringResponse setCateringStatus(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                              @PathVariable @Positive Long id, @Valid @RequestBody PackageStatusRequest request) {
        return vendorService.setCateringStatus(principal, id, request);
    }

    @GetMapping("/decoration-packages")
    public List<DecorationResponse> decorations(@AuthenticationPrincipal WeddingHubPrincipal principal) {
        return vendorService.decorations(principal);
    }

    @PostMapping("/decoration-packages")
    @ResponseStatus(HttpStatus.CREATED)
    public DecorationResponse createDecoration(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                               @Valid @RequestBody DecorationRequest request) {
        return vendorService.createDecoration(principal, request);
    }

    @PutMapping("/decoration-packages/{id}")
    public DecorationResponse updateDecoration(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                               @PathVariable @Positive Long id, @Valid @RequestBody DecorationRequest request) {
        return vendorService.updateDecoration(principal, id, request);
    }

    @PatchMapping("/decoration-packages/{id}/status")
    public DecorationResponse setDecorationStatus(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                                  @PathVariable @Positive Long id, @Valid @RequestBody PackageStatusRequest request) {
        return vendorService.setDecorationStatus(principal, id, request);
    }
}
