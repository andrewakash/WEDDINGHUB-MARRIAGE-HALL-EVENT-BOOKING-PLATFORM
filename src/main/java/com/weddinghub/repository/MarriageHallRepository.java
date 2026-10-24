package com.weddinghub.repository;

import com.weddinghub.entity.MarriageHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MarriageHallRepository extends JpaRepository<MarriageHall,Long> {
    @Query("""
            select h from MarriageHall h
            where h.status = com.weddinghub.entity.HallStatus.ACTIVE
              and h.vendor.approvalStatus = com.weddinghub.entity.VendorApprovalStatus.APPROVED
              and (:city is null or lower(h.city) like lower(concat('%', :city, '%')))
              and (:minimumCapacity is null or h.capacity >= :minimumCapacity)
              and (:maximumPrice is null or h.pricePerDay <= :maximumPrice)
            order by h.pricePerDay asc, h.name asc
            """)
    List<MarriageHall> searchActiveHalls(@Param("city") String city,
                                         @Param("minimumCapacity") Integer minimumCapacity,
                                         @Param("maximumPrice") BigDecimal maximumPrice);

    Optional<MarriageHall> findByIdAndStatus(Long id, com.weddinghub.entity.HallStatus status);
    @Query("select h from MarriageHall h where h.id = :id and h.status = com.weddinghub.entity.HallStatus.ACTIVE and h.vendor.approvalStatus = com.weddinghub.entity.VendorApprovalStatus.APPROVED")
    Optional<MarriageHall> findPublicHall(@Param("id") Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from MarriageHall h where h.id = :id and h.status = com.weddinghub.entity.HallStatus.ACTIVE and h.vendor.approvalStatus = com.weddinghub.entity.VendorApprovalStatus.APPROVED")
    Optional<MarriageHall> lockPublicHall(@Param("id") Long id);
    Optional<MarriageHall> findByIdAndVendorId(Long id, Long vendorId);
    List<MarriageHall> findByVendorIdOrderByNameAsc(Long vendorId);
}
