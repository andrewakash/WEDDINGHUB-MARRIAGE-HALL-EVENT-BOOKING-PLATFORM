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
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hall_id", nullable = false)
    private MarriageHall hall;

    @Column(name = "event_start", nullable = false)
    private LocalDateTime eventStart;

    @Column(name = "event_end", nullable = false)
    private LocalDateTime eventEnd;

    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    @Column(name = "hall_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal hallPrice;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Booking() { }

    public Booking(User customer, MarriageHall hall, LocalDateTime eventStart, LocalDateTime eventEnd,
                   Integer guestCount, BigDecimal hallPrice) {
        this.customer = customer;
        this.hall = hall;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.guestCount = guestCount;
        this.hallPrice = hallPrice;
        this.totalAmount = hallPrice;
        this.status = BookingStatus.PENDING;
    }

    public Long getId() { return id; }
    public User getCustomer() { return customer; }
    public MarriageHall getHall() { return hall; }
    public LocalDateTime getEventStart() { return eventStart; }
    public LocalDateTime getEventEnd() { return eventEnd; }
    public Integer getGuestCount() { return guestCount; }
    public BigDecimal getHallPrice() { return hallPrice; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(BookingStatus status) { this.status = status; }
}
