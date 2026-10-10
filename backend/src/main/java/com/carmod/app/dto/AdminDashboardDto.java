package com.carmod.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {

    private long totalCustomers;
    private long totalVehicles;
    private long totalRequests;
    private long pendingRequests;
    private long approvedRequests;
    private long inProgressRequests;
    private long completedRequests;
    private long rejectedRequests;
    private BigDecimal totalRevenue;
    private Double averageRating;

    // Chart data
    private List<Integer> monthlyRequestCounts; // 12 months
    private Map<String, Long> requestsByStatus;
}
