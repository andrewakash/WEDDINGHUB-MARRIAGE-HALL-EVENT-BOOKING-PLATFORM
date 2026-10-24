
package com.weddinghub.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "marriage_halls")
public class MarriageHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorProfile vendor;

    @Column(nullable = false, length = 150)
    private String name;

    private String description;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "price_per_day", nullable = false)
    private BigDecimal pricePerDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HallStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public MarriageHall() {
    }

    public MarriageHall(VendorProfile vendor, String name, String description, String city, String address,
                        Integer capacity, BigDecimal pricePerDay) {
        this.vendor = vendor;
        this.name = name;
        this.description = description;
        this.city = city;
        this.address = address;
        this.capacity = capacity;
        this.pricePerDay = pricePerDay;
        this.status = HallStatus.INACTIVE;
    }

    public Long getId() {
        return id;
    }

    public VendorProfile getVendor() {
        return vendor;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public HallStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void update(String name, String description, String city, String address, Integer capacity, BigDecimal pricePerDay) {
        this.name = name;
        this.description = description;
        this.city = city;
        this.address = address;
        this.capacity = capacity;
        this.pricePerDay = pricePerDay;
    }

    public void setStatus(HallStatus status) {
        this.status = status;
    }
}
