package com.weddinghub.repository;

import java.util.List;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.weddinghub.entity.CateringPackage;
import com.weddinghub.entity.HallStatus;

public interface CateringPackageRepository extends JpaRepository<CateringPackage, Long> {
    @Query("select p from CateringPackage p where p.status = :status and p.vendor.approvalStatus = com.weddinghub.entity.VendorApprovalStatus.APPROVED order by p.name")
    List<CateringPackage> findPublicPackages(@Param("status") HallStatus status);
    Optional<CateringPackage> findByIdAndVendorId(Long id, Long vendorId);
    List<CateringPackage> findByVendorIdOrderByCreatedAtDesc(Long vendorId);
}
