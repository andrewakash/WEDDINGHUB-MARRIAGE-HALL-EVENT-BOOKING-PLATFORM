package com.weddinghub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.weddinghub.entity.BookingStatus;
import com.weddinghub.entity.CateringPackage;
import com.weddinghub.entity.DecorationPackage;
import com.weddinghub.entity.MarriageHall;
import com.weddinghub.entity.Payment;
import com.weddinghub.entity.PaymentStatus;
import com.weddinghub.entity.VendorApprovalStatus;
import com.weddinghub.entity.VendorProfile;
import com.weddinghub.entity.UserRole;
import com.weddinghub.repository.CateringPackageRepository;
import com.weddinghub.repository.DecorationPackageRepository;
import com.weddinghub.repository.MarriageHallRepository;
import com.weddinghub.repository.PaymentRepository;
import com.weddinghub.repository.VendorProfileRepository;
import com.weddinghub.repository.UserRepository;

@Service
public class AdminService {
    private final VendorProfileRepository vendors;
    private final PaymentRepository payments;
    private final UserRepository users;
    private final MarriageHallRepository halls;
    private final CateringPackageRepository catering;
    private final DecorationPackageRepository decorations;
    private final BookingService bookingService;

    public AdminService(VendorProfileRepository vendors, PaymentRepository payments,
                        UserRepository users, MarriageHallRepository halls, CateringPackageRepository catering,
                        DecorationPackageRepository decorations, BookingService bookingService) {
        this.vendors = vendors;
        this.payments = payments;
        this.users = users;
        this.halls = halls;
        this.catering = catering;
        this.decorations = decorations;
        this.bookingService = bookingService;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> users(UserRole role) {
        return users.findAll().stream().filter(user -> role == null || user.getRole() == role)
                .map(AuthService::userResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<VendorResponse> vendors(VendorApprovalStatus status) {
        List<VendorProfile> profiles = status == null ? vendors.findAll() : vendors.findByApprovalStatusOrderByCreatedAtAsc(status);
        return profiles.stream().map(AuthService::vendorResponse).toList();
    }

    @Transactional
    public VendorResponse decideVendor(Long id, VendorDecision decision) {
        VendorProfile vendor = vendors.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Vendor profile not found."));
        if (vendor.getApprovalStatus() == VendorApprovalStatus.APPROVED
                && decision.status() != VendorApprovalStatus.APPROVED) {
            throw new IllegalStateException("An approved vendor cannot be moved back to pending or rejected.");
        }
        vendor.setApprovalStatus(decision.status());
        return AuthService.vendorResponse(vendor);
    }

    @Transactional
    public PaymentResponse decidePayment(Long id, PaymentDecision decision) {
        Payment payment = payments.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Payment not found."));
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only pending payments can be updated.");
        }
        if (decision.status() == PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Payment decision must be SUCCESS or FAILED.");
        }
        if (decision.status() == PaymentStatus.SUCCESS && payment.getBooking().getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("A cancelled booking cannot be paid.");
        }
        if (decision.status() == PaymentStatus.SUCCESS
                && payments.existsByBookingIdAndStatus(payment.getBooking().getId(), PaymentStatus.SUCCESS)) {
            throw new IllegalStateException("This booking already has a successful payment.");
        }
        String reference = decision.transactionReference() == null || decision.transactionReference().isBlank()
                ? null : decision.transactionReference().trim();
        if (decision.status() == PaymentStatus.SUCCESS && reference == null) {
            throw new IllegalArgumentException("A transaction reference is required for successful payments.");
        }
        payment.setStatus(decision.status(), reference);
        if (decision.status() == PaymentStatus.SUCCESS && payment.getBooking().getStatus() == BookingStatus.PENDING) {
            payment.getBooking().setStatus(BookingStatus.CONFIRMED);
        }
        return new PaymentResponse(payment.getId(), payment.getAmount(), payment.getPaymentMethod(),
                payment.getStatus(), payment.getTransactionReference(), payment.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<HallResponse> halls() {
        return halls.findAll().stream().map(DtoMapper::hall).toList();
    }

    @Transactional
    public HallResponse hallStatus(Long id, HallStatusRequest request) {
        MarriageHall hall = halls.findById(id).orElseThrow(() -> new java.util.NoSuchElementException("Venue not found."));
        hall.setStatus(request.status());
        return DtoMapper.hall(hall);
    }

    @Transactional(readOnly = true)
    public List<CateringResponse> catering() {
        return catering.findAll().stream().map(DtoMapper::catering).toList();
    }

    @Transactional
    public CateringResponse cateringStatus(Long id, PackageStatusRequest request) {
        CateringPackage item = catering.findById(id).orElseThrow(() -> new java.util.NoSuchElementException("Catering package not found."));
        item.setStatus(request.status());
        return DtoMapper.catering(item);
    }

    @Transactional(readOnly = true)
    public List<DecorationResponse> decorations() {
        return decorations.findAll().stream().map(DtoMapper::decoration).toList();
    }

    @Transactional
    public DecorationResponse decorationStatus(Long id, PackageStatusRequest request) {
        DecorationPackage item = decorations.findById(id).orElseThrow(() -> new java.util.NoSuchElementException("Decoration package not found."));
        item.setStatus(request.status());
        return DtoMapper.decoration(item);
    }

    public List<BookingResponse> bookings() {
        return bookingService.allBookings();
    }

    public BookingResponse bookingStatus(Long id, BookingStatusRequest request) {
        return bookingService.updateAdminStatus(id, request.status());
    }
}
