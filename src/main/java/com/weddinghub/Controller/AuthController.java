package com.weddinghub.Controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.weddinghub.dto.ApiDtos.AccountResponse;
import com.weddinghub.dto.ApiDtos.CustomerRegistration;
import com.weddinghub.dto.ApiDtos.UserResponse;
import com.weddinghub.dto.ApiDtos.VendorRegistration;
import com.weddinghub.security.WeddingHubPrincipal;
import com.weddinghub.service.AuthService;

@Validated
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {

        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse registerCustomer(@Valid @RequestBody CustomerRegistration request) {
        return authService.registerCustomer(request);
    }

    @PostMapping("/vendors/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse registerVendor(@Valid @RequestBody VendorRegistration request) {
        return authService.registerVendor(request);
    }

    @GetMapping("/me")
    public UserResponse currentUser(@AuthenticationPrincipal WeddingHubPrincipal principal) {
        return new UserResponse(principal.id(), principal.fullName(), principal.email(), null,
                principal.role(), null);
    }
}
