package com.weddinghub.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.weddinghub.entity.BookingStatus;
import com.weddinghub.entity.FoodType;
import com.weddinghub.entity.HallStatus;
import com.weddinghub.entity.PaymentMethod;
import com.weddinghub.entity.PaymentStatus;
import com.weddinghub.entity.VendorApprovalStatus;

public final class ApiDtos {
    private ApiDtos() { }

    public record CustomerRegistration(
            @NotBlank @Size(max = 100) String fullName,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(min = 12, max = 72) String password,
            @Pattern(regexp = "^$|[+0-9() -]{7,15}") String phone) { }

    public record VendorRegistration(
            @NotBlank @Size(max = 100) String fullName,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(min = 12, max = 72) String password,
            @Pattern(regexp = "^$|[+0-9() -]{7,15}") String phone,
            @NotBlank @Size(max = 150) String businessName,
            @Size(max = 10000) String businessDescription) { }

    public record UserResponse(Long id, String fullName, String email, String phone, String role, LocalDateTime createdAt) { }
    public record VendorResponse(Long id, Long userId, String businessName, String businessDescription,
                                 VendorApprovalStatus approvalStatus, LocalDateTime createdAt) { }
    public record VendorProfileRequest(@NotBlank @Size(max = 150) String businessName,
                                       @Size(max = 10000) String businessDescription) { }
    public record AccountResponse(UserResponse user, VendorResponse vendor) { }

    public record HallRequest(
            @NotBlank @Size(max = 150) String name,
            @Size(max = 10000) String description,
            @NotBlank @Size(max = 100) String city,
            @NotBlank @Size(max = 255) String address,
            @NotNull @Positive Integer capacity,
            @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 10, fraction = 2) BigDecimal pricePerDay) { }

    public record HallStatusRequest(@NotNull HallStatus status) { }
    public record HallResponse(Long id, Long vendorId, String name, String description, String city,
                               String address, Integer capacity, BigDecimal pricePerDay, HallStatus status,
                               LocalDateTime createdAt) { }

    public record CateringRequest(
            @NotBlank @Size(max = 150) String name,
            @Size(max = 10000) String description,
            @NotNull FoodType foodType,
            @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 8, fraction = 2) BigDecimal pricePerPerson) { }

    public record DecorationRequest(
            @NotBlank @Size(max = 150) String name,
            @Size(max = 10000) String description,
            @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 10, fraction = 2) BigDecimal price) { }

    public record PackageStatusRequest(@NotNull HallStatus status) { }
    public record CateringResponse(Long id, Long vendorId, String vendorName, String name, String description,
                                   FoodType foodType, BigDecimal pricePerPerson, HallStatus status, LocalDateTime createdAt) { }
    public record DecorationResponse(Long id, Long vendorId, String vendorName, String name, String description,
                                     BigDecimal price, HallStatus status, LocalDateTime createdAt) { }

    public record BookingRequest(
            @NotNull @Positive Long hallId,
            @NotNull @Future LocalDateTime eventStart,
            @NotNull @Future LocalDateTime eventEnd,
            @NotNull @Positive Integer guestCount,
            @Positive Long cateringPackageId,
            @Positive Long decorationPackageId) { }

    public record BookingStatusRequest(@NotNull BookingStatus status) { }
    public record BookingCateringResponse(Long packageId, String packageName, Integer guestCount,
                                          BigDecimal agreedPricePerPerson, BigDecimal lineTotal) { }
    public record BookingDecorationResponse(Long packageId, String packageName, BigDecimal agreedPrice) { }
    public record PaymentResponse(Long id, BigDecimal amount, PaymentMethod paymentMethod,
                                  PaymentStatus status, String transactionReference, LocalDateTime createdAt) { }
    public record BookingResponse(Long id, Long customerId, String customerName, Long hallId, String hallName,
                                  String city, LocalDateTime eventStart, LocalDateTime eventEnd, Integer guestCount,
                                  BigDecimal hallPrice, BigDecimal totalAmount, BookingStatus status,
                                  LocalDateTime createdAt, List<BookingCateringResponse> catering,
                                  List<BookingDecorationResponse> decorations, List<PaymentResponse> payments) { }

    public record PaymentRequest(@NotNull PaymentMethod paymentMethod) { }
    public record VendorDecision(@NotNull VendorApprovalStatus status) { }
    public record PaymentDecision(@NotNull PaymentStatus status, @Size(max = 100) String transactionReference) { }
}
