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
@Table(name = "booking_decorations")
public class BookingDecoration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "decoration_package_id", nullable = false)
    private DecorationPackage decorationPackage;

    @Column(name = "agreed_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal agreedPrice;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected BookingDecoration() { }

    public BookingDecoration(Booking booking, DecorationPackage decorationPackage, BigDecimal agreedPrice) {
        this.booking = booking;
        this.decorationPackage = decorationPackage;
        this.agreedPrice = agreedPrice;
    }

    public Long getId() { return id; }
    public Booking getBooking() { return booking; }
    public DecorationPackage getDecorationPackage() { return decorationPackage; }
    public BigDecimal getAgreedPrice() { return agreedPrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
