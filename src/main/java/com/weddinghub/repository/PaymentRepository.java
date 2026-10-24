package com.weddinghub.repository;

import java.util.List;
import com.weddinghub.entity.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weddinghub.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingIdOrderByCreatedAtDesc(Long bookingId);
    boolean existsByBookingIdAndStatus(Long bookingId, PaymentStatus status);
}
