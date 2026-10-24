package com.weddinghub.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vendor_profiles")
public class VendorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "business_name", nullable = false, length = 150)
    private String businessName;

    @Column(name = "business_description", columnDefinition = "text")
    private String businessDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private VendorApprovalStatus approvalStatus = VendorApprovalStatus.PENDING;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected VendorProfile() { }

    public VendorProfile(User user, String businessName, String businessDescription) {
        this.user = user;
        this.businessName = businessName;
        this.businessDescription = businessDescription;
        this.approvalStatus = VendorApprovalStatus.PENDING;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getBusinessName() { return businessName; }
    public String getBusinessDescription() { return businessDescription; }
    public VendorApprovalStatus getApprovalStatus() { return approvalStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setBusinessDescription(String businessDescription) { this.businessDescription = businessDescription; }
    public void setApprovalStatus(VendorApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
}
