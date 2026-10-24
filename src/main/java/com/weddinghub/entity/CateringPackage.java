package com.weddinghub.entity;

import java.math.BigDecimal;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "catering_packages")
public class CateringPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorProfile vendor;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_type", nullable = false)
    private FoodType foodType;

    @Column(name = "price_per_person", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerPerson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HallStatus status = HallStatus.INACTIVE;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected CateringPackage() { }

    public CateringPackage(VendorProfile vendor, String name, String description, FoodType foodType, BigDecimal pricePerPerson) {
        this.vendor = vendor;
        this.name = name;
        this.description = description;
        this.foodType = foodType;
        this.pricePerPerson = pricePerPerson;
        this.status = HallStatus.INACTIVE;
    }

    public Long getId() { return id; }
    public VendorProfile getVendor() { return vendor; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public FoodType getFoodType() { return foodType; }
    public BigDecimal getPricePerPerson() { return pricePerPerson; }
    public HallStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void update(String name, String description, FoodType foodType, BigDecimal pricePerPerson) {
        this.name = name;
        this.description = description;
        this.foodType = foodType;
        this.pricePerPerson = pricePerPerson;
    }
    public void setStatus(HallStatus status) { this.status = status; }
}
