package com.weddinghub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weddinghub.entity.VendorApprovalStatus;
import com.weddinghub.entity.VendorProfile;

public interface VendorProfileRepository extends JpaRepository<VendorProfile, Long> {
    Optional<VendorProfile> findByUserId(Long userId);
    Optional<VendorProfile> findByIdAndUserId(Long id, Long userId);
    List<VendorProfile> findByApprovalStatusOrderByCreatedAtAsc(VendorApprovalStatus status);
}
