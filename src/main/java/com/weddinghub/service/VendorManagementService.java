package com.weddinghub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weddinghub.dto.ApiDtos.CateringRequest;
import com.weddinghub.dto.ApiDtos.CateringResponse;
import com.weddinghub.dto.ApiDtos.DecorationRequest;
import com.weddinghub.dto.ApiDtos.DecorationResponse;
import com.weddinghub.dto.ApiDtos.HallRequest;
import com.weddinghub.dto.ApiDtos.HallResponse;
import com.weddinghub.dto.ApiDtos.HallStatusRequest;
import com.weddinghub.dto.ApiDtos.PackageStatusRequest;
import com.weddinghub.dto.ApiDtos.VendorResponse;
import com.weddinghub.dto.ApiDtos.VendorProfileRequest;
import com.weddinghub.entity.CateringPackage;
import com.weddinghub.entity.DecorationPackage;
import com.weddinghub.entity.HallStatus;
import com.weddinghub.entity.MarriageHall;
import com.weddinghub.entity.VendorApprovalStatus;
import com.weddinghub.entity.VendorProfile;
import com.weddinghub.repository.CateringPackageRepository;
import com.weddinghub.repository.DecorationPackageRepository;
import com.weddinghub.repository.MarriageHallRepository;
import com.weddinghub.repository.VendorProfileRepository;
import com.weddinghub.security.WeddingHubPrincipal;

@Service
public class VendorManagementService {
    private final VendorProfileRepository vendors;
    private final MarriageHallRepository halls;
    private final CateringPackageRepository catering;
    private final DecorationPackageRepository decorations;

    public VendorManagementService(VendorProfileRepository vendors, MarriageHallRepository halls,
                                   CateringPackageRepository catering, DecorationPackageRepository decorations) {
        this.vendors = vendors;
        this.halls = halls;
        this.catering = catering;
        this.decorations = decorations;
    }

    @Transactional(readOnly = true)
    public VendorResponse profile(WeddingHubPrincipal principal) {
        return AuthService.vendorResponse(vendorFor(principal));
    }

    @Transactional
    public VendorResponse updateProfile(WeddingHubPrincipal principal, VendorProfileRequest request) {
        VendorProfile vendor = vendorFor(principal);
        vendor.setBusinessName(request.businessName().trim());
        vendor.setBusinessDescription(request.businessDescription());
        return AuthService.vendorResponse(vendor);
    }

    @Transactional(readOnly = true)
    public List<HallResponse> halls(WeddingHubPrincipal principal) {
        VendorProfile vendor = vendorFor(principal);
        return halls.findByVendorIdOrderByNameAsc(vendor.getId()).stream().map(DtoMapper::hall).toList();
    }

    @Transactional
    public HallResponse createHall(WeddingHubPrincipal principal, HallRequest request) {
        VendorProfile vendor = approvedVendor(principal);
        MarriageHall hall = halls.save(new MarriageHall(vendor, request.name().trim(), request.description(),
                request.city().trim(), request.address().trim(), request.capacity(), request.pricePerDay()));
        return DtoMapper.hall(hall);
    }

    @Transactional
    public HallResponse updateHall(WeddingHubPrincipal principal, Long id, HallRequest request) {
        VendorProfile vendor = approvedVendor(principal);
        MarriageHall hall = halls.findByIdAndVendorId(id, vendor.getId())
                .orElseThrow(() -> new java.util.NoSuchElementException("Venue not found."));
        hall.update(request.name().trim(), request.description(), request.city().trim(), request.address().trim(),
                request.capacity(), request.pricePerDay());
        return DtoMapper.hall(hall);
    }

    @Transactional
    public HallResponse setHallStatus(WeddingHubPrincipal principal, Long id, HallStatusRequest request) {
        VendorProfile vendor = approvedVendor(principal);
        MarriageHall hall = halls.findByIdAndVendorId(id, vendor.getId())
                .orElseThrow(() -> new java.util.NoSuchElementException("Venue not found."));
        hall.setStatus(request.status());
        return DtoMapper.hall(hall);
    }

    @Transactional(readOnly = true)
    public List<CateringResponse> catering(WeddingHubPrincipal principal) {
        VendorProfile vendor = vendorFor(principal);
        return catering.findByVendorIdOrderByCreatedAtDesc(vendor.getId()).stream().map(DtoMapper::catering).toList();
    }

    @Transactional
    public CateringResponse createCatering(WeddingHubPrincipal principal, CateringRequest request) {
        VendorProfile vendor = approvedVendor(principal);
        CateringPackage item = catering.save(new CateringPackage(vendor, request.name().trim(), request.description(),
                request.foodType(), request.pricePerPerson()));
        return DtoMapper.catering(item);
    }

    @Transactional
    public CateringResponse updateCatering(WeddingHubPrincipal principal, Long id, CateringRequest request) {
        VendorProfile vendor = approvedVendor(principal);
        CateringPackage item = catering.findByIdAndVendorId(id, vendor.getId())
                .orElseThrow(() -> new java.util.NoSuchElementException("Catering package not found."));
        item.update(request.name().trim(), request.description(), request.foodType(), request.pricePerPerson());
        return DtoMapper.catering(item);
    }

    @Transactional
    public CateringResponse setCateringStatus(WeddingHubPrincipal principal, Long id, PackageStatusRequest request) {
        VendorProfile vendor = approvedVendor(principal);
        CateringPackage item = catering.findByIdAndVendorId(id, vendor.getId())
                .orElseThrow(() -> new java.util.NoSuchElementException("Catering package not found."));
        item.setStatus(request.status());
        return DtoMapper.catering(item);
    }

    @Transactional(readOnly = true)
    public List<DecorationResponse> decorations(WeddingHubPrincipal principal) {
        VendorProfile vendor = vendorFor(principal);
        return decorations.findByVendorIdOrderByCreatedAtDesc(vendor.getId()).stream().map(DtoMapper::decoration).toList();
    }

    @Transactional
    public DecorationResponse createDecoration(WeddingHubPrincipal principal, DecorationRequest request) {
        VendorProfile vendor = approvedVendor(principal);
        DecorationPackage item = decorations.save(new DecorationPackage(vendor, request.name().trim(), request.description(), request.price()));
        return DtoMapper.decoration(item);
    }

    @Transactional
    public DecorationResponse updateDecoration(WeddingHubPrincipal principal, Long id, DecorationRequest request) {
        VendorProfile vendor = approvedVendor(principal);
        DecorationPackage item = decorations.findByIdAndVendorId(id, vendor.getId())
                .orElseThrow(() -> new java.util.NoSuchElementException("Decoration package not found."));
        item.update(request.name().trim(), request.description(), request.price());
        return DtoMapper.decoration(item);
    }

    @Transactional
    public DecorationResponse setDecorationStatus(WeddingHubPrincipal principal, Long id, PackageStatusRequest request) {
        VendorProfile vendor = approvedVendor(principal);
        DecorationPackage item = decorations.findByIdAndVendorId(id, vendor.getId())
                .orElseThrow(() -> new java.util.NoSuchElementException("Decoration package not found."));
        item.setStatus(request.status());
        return DtoMapper.decoration(item);
    }

    @Transactional(readOnly = true)
    public List<CateringResponse> publicCatering() {
        return catering.findPublicPackages(HallStatus.ACTIVE).stream().map(DtoMapper::catering).toList();
    }

    @Transactional(readOnly = true)
    public CateringResponse publicCatering(Long id) {
        CateringPackage item = catering.findById(id)
                .filter(value -> value.getStatus() == HallStatus.ACTIVE
                        && value.getVendor().getApprovalStatus() == VendorApprovalStatus.APPROVED)
                .orElseThrow(() -> new java.util.NoSuchElementException("Catering package not found."));
        return DtoMapper.catering(item);
    }

    @Transactional(readOnly = true)
    public List<DecorationResponse> publicDecorations() {
        return decorations.findPublicPackages(HallStatus.ACTIVE).stream().map(DtoMapper::decoration).toList();
    }

    @Transactional(readOnly = true)
    public DecorationResponse publicDecoration(Long id) {
        DecorationPackage item = decorations.findById(id)
                .filter(value -> value.getStatus() == HallStatus.ACTIVE
                        && value.getVendor().getApprovalStatus() == VendorApprovalStatus.APPROVED)
                .orElseThrow(() -> new java.util.NoSuchElementException("Decoration package not found."));
        return DtoMapper.decoration(item);
    }

    private VendorProfile vendorFor(WeddingHubPrincipal principal) {
        return vendors.findByUserId(principal.id())
                .orElseThrow(() -> new java.util.NoSuchElementException("Vendor profile not found."));
    }

    private VendorProfile approvedVendor(WeddingHubPrincipal principal) {
        VendorProfile vendor = vendorFor(principal);
        if (vendor.getApprovalStatus() != VendorApprovalStatus.APPROVED) {
            throw new IllegalStateException("Your vendor profile must be approved before managing listings.");
        }
        return vendor;
    }
}
