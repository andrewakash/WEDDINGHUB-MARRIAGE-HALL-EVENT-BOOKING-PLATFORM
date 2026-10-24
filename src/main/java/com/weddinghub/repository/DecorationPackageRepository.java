package com.weddinghub.repository;

import java.util.List;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.weddinghub.entity.DecorationPackage;
import com.weddinghub.entity.HallStatus;

public interface DecorationPackageRepository extends JpaRepository<DecorationPackage, Long> {
    @Query("select p from DecorationPackage p " +
            "where p.status = :status and" +
            " p.vendor.approvalStatus = com.weddinghub.entity.VendorApprovalStatus.APPROVED order by p.name")
    List<DecorationPackage> findPublicPackages(@Param("status") HallStatus status);
    Optional<DecorationPackage> findByIdAndVendorId(Long id, Long vendorId);
    List<DecorationPackage> findByVendorIdOrderByCreatedAtDesc(Long vendorId);
}
