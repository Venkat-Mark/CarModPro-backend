package com.carmod.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modification_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(precision = 12, scale = 2)
    private BigDecimal estimatedCost;

    @Column(precision = 12, scale = 2)
    private BigDecimal finalCost;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    @Column(length = 1000)
    private String customerNotes;

    @Size(max = 1000, message = "Admin notes cannot exceed 1000 characters")
    @Column(length = 1000)
    private String adminNotes;

    @Column(length = 100)
    private String rejectionReason;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestDate;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "modificationRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<RequestItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Feedback feedback;

    public enum RequestStatus {
        PENDING, APPROVED, IN_PROGRESS, COMPLETED, REJECTED, CANCELLED
    }

    public boolean canBeCancelled() {
        return status == RequestStatus.PENDING || status == RequestStatus.APPROVED;
    }

    public boolean isCompleted() {
        return status == RequestStatus.COMPLETED;
    }
}
