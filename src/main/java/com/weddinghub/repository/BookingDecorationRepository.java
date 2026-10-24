package com.weddinghub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.weddinghub.entity.BookingDecoration;

public interface BookingDecorationRepository extends JpaRepository<BookingDecoration, Long> {
    List<BookingDecoration> findByBookingId(Long bookingId);
}
