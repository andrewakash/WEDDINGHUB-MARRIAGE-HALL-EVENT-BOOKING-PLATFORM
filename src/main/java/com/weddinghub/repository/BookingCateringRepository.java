package com.weddinghub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.weddinghub.entity.BookingCatering;

public interface BookingCateringRepository extends JpaRepository<BookingCatering, Long> {
    List<BookingCatering> findByBookingId(Long bookingId);
}
