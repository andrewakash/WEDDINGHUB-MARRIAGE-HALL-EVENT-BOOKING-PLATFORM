package com.weddinghub.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking_catering")
public class BookingCatering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "catering_package_id", nullable = false)
    private CateringPackage cateringPackage;

    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    @Column(name = "agreed_price_per_person", nullable = false, precision = 10, scale = 2)
    private BigDecimal agreedPricePerPerson;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected BookingCatering() { }

    public BookingCatering(Booking booking, CateringPackage cateringPackage, Integer guestCount, BigDecimal agreedPricePerPerson) {
        this.booking = booking;
        this.cateringPackage = cateringPackage;
        this.guestCount = guestCount;
        this.agreedPricePerPerson = agreedPricePerPerson;
    }

    public Long getId() { return id; }
    public Booking getBooking() { return booking; }
    public CateringPackage getCateringPackage() { return cateringPackage; }
    public Integer getGuestCount() { return guestCount; }
    public BigDecimal getAgreedPricePerPerson() { return agreedPricePerPerson; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
