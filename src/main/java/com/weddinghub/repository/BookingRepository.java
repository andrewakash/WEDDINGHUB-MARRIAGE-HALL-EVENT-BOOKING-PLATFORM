package com.weddinghub.repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weddinghub.entity.Booking;
import com.weddinghub.entity.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    Optional<Booking> findByIdAndCustomerId(Long id, Long customerId);
    List<Booking> findByHallVendorIdOrderByCreatedAtDesc(Long vendorId);
    List<Booking> findAllByOrderByCreatedAtDesc();
    Optional<Booking> findByIdAndHallVendorId(Long id, Long vendorId);
    boolean existsByHallIdAndStatusInAndEventStartLessThanAndEventEndGreaterThan(
            Long hallId, Collection<BookingStatus> statuses, LocalDateTime requestedEnd, LocalDateTime requestedStart);
}
