package com.weddinghub.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weddinghub.dto.ApiDtos.BookingCateringResponse;
import com.weddinghub.dto.ApiDtos.BookingDecorationResponse;
import com.weddinghub.dto.ApiDtos.BookingRequest;
import com.weddinghub.dto.ApiDtos.BookingResponse;
import com.weddinghub.dto.ApiDtos.BookingStatusRequest;
import com.weddinghub.dto.ApiDtos.PaymentRequest;
import com.weddinghub.dto.ApiDtos.PaymentResponse;
import com.weddinghub.entity.Booking;
import com.weddinghub.entity.BookingCatering;
import com.weddinghub.entity.BookingDecoration;
import com.weddinghub.entity.BookingStatus;
import com.weddinghub.entity.CateringPackage;
import com.weddinghub.entity.DecorationPackage;
import com.weddinghub.entity.HallStatus;
import com.weddinghub.entity.MarriageHall;
import com.weddinghub.entity.Payment;
import com.weddinghub.entity.PaymentStatus;
import com.weddinghub.entity.User;
import com.weddinghub.entity.VendorApprovalStatus;
import com.weddinghub.repository.BookingCateringRepository;
import com.weddinghub.repository.BookingDecorationRepository;
import com.weddinghub.repository.BookingRepository;
import com.weddinghub.repository.CateringPackageRepository;
import com.weddinghub.repository.DecorationPackageRepository;
import com.weddinghub.repository.MarriageHallRepository;
import com.weddinghub.repository.PaymentRepository;
import com.weddinghub.repository.UserRepository;
import com.weddinghub.repository.VendorProfileRepository;
import com.weddinghub.security.WeddingHubPrincipal;

@Service
public class BookingService {
    private static final List<BookingStatus> BLOCKING_STATUSES = List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final BookingRepository bookings;
    private final BookingCateringRepository bookingCatering;
    private final BookingDecorationRepository bookingDecorations;
    private final MarriageHallRepository halls;
    private final CateringPackageRepository cateringPackages;
    private final DecorationPackageRepository decorationPackages;
    private final PaymentRepository payments;
    private final UserRepository users;
    private final VendorProfileRepository vendors;

    public BookingService(BookingRepository bookings, BookingCateringRepository bookingCatering,
                          BookingDecorationRepository bookingDecorations, MarriageHallRepository halls,
                          CateringPackageRepository cateringPackages, DecorationPackageRepository decorationPackages,
                          PaymentRepository payments, UserRepository users, VendorProfileRepository vendors) {
        this.bookings = bookings;
        this.bookingCatering = bookingCatering;
        this.bookingDecorations = bookingDecorations;
        this.halls = halls;
        this.cateringPackages = cateringPackages;
        this.decorationPackages = decorationPackages;
        this.payments = payments;
        this.users = users;
        this.vendors = vendors;
    }

    @Transactional
    public BookingResponse create(WeddingHubPrincipal principal, BookingRequest request) {
        if (!request.eventEnd().isAfter(request.eventStart())) {
            throw new IllegalArgumentException("Event end must be after event start.");
        }

        MarriageHall hall = halls.lockPublicHall(request.hallId())
                .orElseThrow(() -> new java.util.NoSuchElementException("Active venue not found."));
        if (request.guestCount() > hall.getCapacity()) {
            throw new IllegalArgumentException("Guest count exceeds this venue's capacity.");
        }
        if (bookings.existsByHallIdAndStatusInAndEventStartLessThanAndEventEndGreaterThan(
                hall.getId(), BLOCKING_STATUSES, request.eventEnd(), request.eventStart())) {
            throw new IllegalStateException("This venue is already reserved for part of that time.");
        }

        User customer = users.findById(principal.id())
                .orElseThrow(() -> new java.util.NoSuchElementException("Customer account not found."));
        long billableDays = Math.max(1, (Duration.between(request.eventStart(), request.eventEnd()).toMinutes() + 1439) / 1440);
        BigDecimal hallPrice = hall.getPricePerDay().multiply(BigDecimal.valueOf(billableDays));
        Booking booking = bookings.save(new Booking(customer, hall, request.eventStart(), request.eventEnd(),
                request.guestCount(), hallPrice));
        BigDecimal total = hallPrice;

        if (request.cateringPackageId() != null) {
            CateringPackage catering = activeCatering(request.cateringPackageId());
            BigDecimal lineTotal = catering.getPricePerPerson().multiply(BigDecimal.valueOf(request.guestCount()));
            bookingCatering.save(new BookingCatering(booking, catering, request.guestCount(), catering.getPricePerPerson()));
            total = total.add(lineTotal);
        }
        if (request.decorationPackageId() != null) {
            DecorationPackage decoration = activeDecoration(request.decorationPackageId());
            bookingDecorations.save(new BookingDecoration(booking, decoration, decoration.getPrice()));
            total = total.add(decoration.getPrice());
        }
        booking.setTotalAmount(total.setScale(2, RoundingMode.HALF_UP));
        return response(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> myBookings(WeddingHubPrincipal principal) {
        return bookings.findByCustomerIdOrderByCreatedAtDesc(principal.id()).stream().map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse myBooking(WeddingHubPrincipal principal, Long id) {
        return bookings.findByIdAndCustomerId(id, principal.id())
                .map(this::response)
                .orElseThrow(() -> new java.util.NoSuchElementException("Booking not found."));
    }

    @Transactional
    public BookingResponse cancel(WeddingHubPrincipal principal, Long id) {
        Booking booking = bookings.findByIdAndCustomerId(id, principal.id())
                .orElseThrow(() -> new java.util.NoSuchElementException("Booking not found."));
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be cancelled by the customer.");
        }
        if (payments.existsByBookingIdAndStatus(id, PaymentStatus.SUCCESS)) {
            throw new IllegalStateException("A booking with a successful payment must be cancelled by support.");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return response(booking);
    }

    @Transactional
    public PaymentResponse startPayment(WeddingHubPrincipal principal, Long bookingId, PaymentRequest request) {
        Booking booking = bookings.findByIdAndCustomerId(bookingId, principal.id())
                .orElseThrow(() -> new java.util.NoSuchElementException("Booking not found."));
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Payment is unavailable for this booking.");
        }
        if (payments.existsByBookingIdAndStatus(bookingId, PaymentStatus.PENDING)) {
            throw new IllegalStateException("A payment is already pending for this booking.");
        }
        if (payments.existsByBookingIdAndStatus(bookingId, PaymentStatus.SUCCESS)) {
            throw new IllegalStateException("This booking already has a successful payment.");
        }
        if (request.paymentMethod() != com.weddinghub.entity.PaymentMethod.DEMO) {
            throw new IllegalArgumentException("No live payment gateway is configured. Use DEMO for development payments.");
        }
        Payment payment = payments.save(new Payment(booking, booking.getTotalAmount(), request.paymentMethod()));
        return paymentResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> vendorBookings(WeddingHubPrincipal principal) {
        Long vendorId = approvedVendorId(principal);
        return bookings.findByHallVendorIdOrderByCreatedAtDesc(vendorId).stream().map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> allBookings() {
        return bookings.findAllByOrderByCreatedAtDesc().stream().map(this::response).toList();
    }

    @Transactional
    public BookingResponse updateAdminStatus(Long id, BookingStatus requestedStatus) {
        Booking booking = bookings.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Booking not found."));
        boolean validTransition = (booking.getStatus() == BookingStatus.PENDING
                && (requestedStatus == BookingStatus.CONFIRMED || requestedStatus == BookingStatus.CANCELLED))
                || (booking.getStatus() == BookingStatus.CONFIRMED
                && (requestedStatus == BookingStatus.COMPLETED || requestedStatus == BookingStatus.CANCELLED));
        if (!validTransition) {
            throw new IllegalStateException("That booking status change is not allowed.");
        }
        if (requestedStatus == BookingStatus.CANCELLED
                && payments.existsByBookingIdAndStatus(id, PaymentStatus.SUCCESS)) {
            throw new IllegalStateException("A paid booking cannot be cancelled until its payment is refunded.");
        }
        booking.setStatus(requestedStatus);
        return response(booking);
    }

    @Transactional
    public BookingResponse updateVendorStatus(WeddingHubPrincipal principal, Long id, BookingStatusRequest request) {
        Long vendorId = approvedVendorId(principal);
        Booking booking = bookings.findByIdAndHallVendorId(id, vendorId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Booking not found."));
        boolean validTransition = (booking.getStatus() == BookingStatus.PENDING
                && (request.status() == BookingStatus.CONFIRMED || request.status() == BookingStatus.CANCELLED))
                || (booking.getStatus() == BookingStatus.CONFIRMED && request.status() == BookingStatus.COMPLETED);
        if (!validTransition) {
            throw new IllegalStateException("That booking status change is not allowed.");
        }
        if (request.status() == BookingStatus.CANCELLED
                && payments.existsByBookingIdAndStatus(id, PaymentStatus.SUCCESS)) {
            throw new IllegalStateException("A paid booking must be cancelled by support.");
        }
        booking.setStatus(request.status());
        return response(booking);
    }

    private CateringPackage activeCatering(Long id) {
        CateringPackage item = cateringPackages.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Catering package not found."));
        if (item.getStatus() != HallStatus.ACTIVE || item.getVendor().getApprovalStatus() != VendorApprovalStatus.APPROVED) {
            throw new IllegalArgumentException("Catering package is not available for booking.");
        }
        return item;
    }

    private DecorationPackage activeDecoration(Long id) {
        DecorationPackage item = decorationPackages.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Decoration package not found."));
        if (item.getStatus() != HallStatus.ACTIVE || item.getVendor().getApprovalStatus() != VendorApprovalStatus.APPROVED) {
            throw new IllegalArgumentException("Decoration package is not available for booking.");
        }
        return item;
    }

    private Long approvedVendorId(WeddingHubPrincipal principal) {
        var profile = vendors.findByUserId(principal.id())
                .orElseThrow(() -> new java.util.NoSuchElementException("Vendor profile not found."));
        if (profile.getApprovalStatus() != VendorApprovalStatus.APPROVED) {
            throw new IllegalStateException("Your vendor profile must be approved to access bookings.");
        }
        return profile.getId();
    }

    private BookingResponse response(Booking booking) {
        List<BookingCateringResponse> catering = bookingCatering.findByBookingId(booking.getId()).stream()
                .map(line -> new BookingCateringResponse(line.getCateringPackage().getId(),
                        line.getCateringPackage().getName(), line.getGuestCount(), line.getAgreedPricePerPerson(),
                        line.getAgreedPricePerPerson().multiply(BigDecimal.valueOf(line.getGuestCount())))).toList();
        List<BookingDecorationResponse> decorations = bookingDecorations.findByBookingId(booking.getId()).stream()
                .map(line -> new BookingDecorationResponse(line.getDecorationPackage().getId(),
                        line.getDecorationPackage().getName(), line.getAgreedPrice())).toList();
        List<PaymentResponse> paymentViews = payments.findByBookingIdOrderByCreatedAtDesc(booking.getId()).stream()
                .map(BookingService::paymentResponse).toList();
        return new BookingResponse(booking.getId(), booking.getCustomer().getId(), booking.getCustomer().getFullName(),
                booking.getHall().getId(), booking.getHall().getName(), booking.getHall().getCity(),
                booking.getEventStart(), booking.getEventEnd(), booking.getGuestCount(), booking.getHallPrice(),
                booking.getTotalAmount(), booking.getStatus(), booking.getCreatedAt(), catering, decorations, paymentViews);
    }

    private static PaymentResponse paymentResponse(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getAmount(), payment.getPaymentMethod(),
                payment.getStatus(), payment.getTransactionReference(), payment.getCreatedAt());
    }
}
