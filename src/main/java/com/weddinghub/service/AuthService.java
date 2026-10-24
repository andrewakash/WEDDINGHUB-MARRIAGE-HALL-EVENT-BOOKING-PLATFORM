package com.weddinghub.service;

import java.util.Locale;
import java.nio.charset.StandardCharsets;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weddinghub.dto.ApiDtos.AccountResponse;
import com.weddinghub.dto.ApiDtos.CustomerRegistration;
import com.weddinghub.dto.ApiDtos.UserResponse;
import com.weddinghub.dto.ApiDtos.VendorRegistration;
import com.weddinghub.dto.ApiDtos.VendorResponse;
import com.weddinghub.entity.User;
import com.weddinghub.entity.UserRole;
import com.weddinghub.entity.VendorProfile;
import com.weddinghub.repository.UserRepository;
import com.weddinghub.repository.VendorProfileRepository;

@Service
public class AuthService {
    private final UserRepository users;
    private final VendorProfileRepository vendors;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository users, VendorProfileRepository vendors, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.vendors = vendors;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AccountResponse registerCustomer(CustomerRegistration request) {
        ensureEmailAvailable(request.email());
        ensurePasswordLength(request.password());
        User user = users.save(new User(request.fullName().trim(), normalizeEmail(request.email()),
                passwordEncoder.encode(request.password()), blankToNull(request.phone()), UserRole.CUSTOMER));
        return new AccountResponse(userResponse(user), null);
    }

    @Transactional
    public AccountResponse registerVendor(VendorRegistration request) {
        ensureEmailAvailable(request.email());
        ensurePasswordLength(request.password());
        User user = users.save(new User(request.fullName().trim(), normalizeEmail(request.email()),
                passwordEncoder.encode(request.password()), blankToNull(request.phone()), UserRole.VENDOR));
        VendorProfile vendor = vendors.save(new VendorProfile(user, request.businessName().trim(), request.businessDescription()));
        return new AccountResponse(userResponse(user), vendorResponse(vendor));
    }

    private void ensureEmailAvailable(String email) {
        if (users.existsByEmailIgnoreCase(normalizeEmail(email))) {
            throw new IllegalStateException("An account with that email already exists.");
        }
    }

    private void ensurePasswordLength(String password) {
        int byteLength = password.getBytes(StandardCharsets.UTF_8).length;
        if (byteLength < 12 || byteLength > 72) {
            throw new IllegalArgumentException("Password must be 12–72 bytes when encoded as UTF-8.");
        }
    }

    public static UserResponse userResponse(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(),
                user.getRole().name(), user.getCreatedAt());
    }

    public static VendorResponse vendorResponse(VendorProfile vendor) {
        return new VendorResponse(vendor.getId(), vendor.getUser().getId(), vendor.getBusinessName(),
                vendor.getBusinessDescription(), vendor.getApprovalStatus(), vendor.getCreatedAt());
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
