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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.weddinghub.dto.ApiDtos.BookingRequest;
import com.weddinghub.dto.ApiDtos.BookingResponse;
import com.weddinghub.dto.ApiDtos.PaymentRequest;
import com.weddinghub.dto.ApiDtos.PaymentResponse;
import com.weddinghub.security.WeddingHubPrincipal;
import com.weddinghub.service.BookingService;

@Validated
@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingResponse> myBookings(@AuthenticationPrincipal WeddingHubPrincipal principal) {
        return bookingService.myBookings(principal);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                  @Valid @RequestBody BookingRequest request) {
        return bookingService.create(principal, request);
    }

    @GetMapping("/{id}")
    public BookingResponse get(@AuthenticationPrincipal WeddingHubPrincipal principal,
                               @PathVariable @Positive Long id) {
        return bookingService.myBooking(principal, id);
    }

    @PostMapping("/{id}/cancel")
    public BookingResponse cancel(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                  @PathVariable @Positive Long id) {
        return bookingService.cancel(principal, id);
    }

    @PostMapping("/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse startPayment(@AuthenticationPrincipal WeddingHubPrincipal principal,
                                        @PathVariable @Positive Long id, @Valid @RequestBody PaymentRequest request) {
        return bookingService.startPayment(principal, id, request);
    }
}
