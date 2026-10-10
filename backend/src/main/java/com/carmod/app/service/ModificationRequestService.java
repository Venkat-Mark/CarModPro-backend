package com.carmod.app.service;

import com.carmod.app.dto.AdminDashboardDto;
import com.carmod.app.dto.ModificationRequestDto;
import com.carmod.app.dto.RequestStatusUpdateDto;
import com.carmod.app.entity.*;
import com.carmod.app.entity.ModificationRequest.RequestStatus;
import com.carmod.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ModificationRequestService {

    @Autowired private ModificationRequestRepository requestRepository;
    @Autowired private RequestItemRepository requestItemRepository;
    @Autowired private ModificationServiceRepository serviceRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private UserRepository userRepository;

    /**
     * Creates a new modification request for a customer
     */
    public ModificationRequest createRequest(ModificationRequestDto dto, Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        // Security: ensure the vehicle belongs to this customer
        if (!vehicle.getOwner().getId().equals(customerId)) {
            throw new SecurityException("Access denied: vehicle does not belong to you");
        }

        List<com.carmod.app.entity.ModificationService> services =
                serviceRepository.findAllById(dto.getServiceIds());

        if (services.isEmpty()) {
            throw new IllegalArgumentException("No valid services selected");
        }

        // Calculate estimated cost
        BigDecimal estimatedCost = services.stream()
                .map(com.carmod.app.entity.ModificationService::getBasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create request
        ModificationRequest request = new ModificationRequest();
        request.setCustomer(customer);
        request.setVehicle(vehicle);
        request.setStatus(RequestStatus.PENDING);
        request.setEstimatedCost(estimatedCost);
        request.setCustomerNotes(dto.getCustomerNotes());

        request = requestRepository.save(request);

        // Create request items
        for (com.carmod.app.entity.ModificationService service : services) {
            RequestItem item = new RequestItem();
            item.setModificationRequest(request);
            item.setService(service);
            item.setQuantity(1);
            item.setUnitPrice(service.getBasePrice());
            item.setSubtotal(service.getBasePrice());
            requestItemRepository.save(item);
        }

        return request;
    }

    /**
     * Calculates estimated cost for selected services (preview before submission)
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateEstimate(List<Long> serviceIds) {
        List<com.carmod.app.entity.ModificationService> services = serviceRepository.findAllById(serviceIds);
        return services.stream()
                .map(com.carmod.app.entity.ModificationService::getBasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Cancels a request (customer action)
     */
    public void cancelRequest(Long requestId, Long customerId) {
        ModificationRequest request = requestRepository.findByIdAndCustomerId(requestId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found or access denied"));

        if (!request.canBeCancelled()) {
            throw new IllegalStateException("This request cannot be cancelled at its current status: " + request.getStatus());
        }

        request.setStatus(RequestStatus.CANCELLED);
        requestRepository.save(request);
    }

    /**
     * Admin: updates request status
     */
    public ModificationRequest updateStatus(RequestStatusUpdateDto dto) {
        ModificationRequest request = requestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        request.setStatus(dto.getStatus());

        if (dto.getAdminNotes() != null && !dto.getAdminNotes().isBlank()) {
            request.setAdminNotes(dto.getAdminNotes());
        }
        if (dto.getRejectionReason() != null && !dto.getRejectionReason().isBlank()) {
            request.setRejectionReason(dto.getRejectionReason());
        }
        if (dto.getFinalCost() != null) {
            request.setFinalCost(dto.getFinalCost());
        }

        if (dto.getStatus() == RequestStatus.COMPLETED) {
            request.setCompletedAt(LocalDateTime.now());
            // If no final cost set, use estimated
            if (request.getFinalCost() == null) {
                request.setFinalCost(request.getEstimatedCost());
            }
        }

        return requestRepository.save(request);
    }

    // ========== Read Operations ==========

    @Transactional(readOnly = true)
    public Page<ModificationRequest> getCustomerRequests(Long customerId, Pageable pageable) {
        return requestRepository.findByCustomerId(customerId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<ModificationRequest> findByIdAndCustomer(Long requestId, Long customerId) {
        return requestRepository.findByIdAndCustomerId(requestId, customerId);
    }

    @Transactional(readOnly = true)
    public Optional<ModificationRequest> findById(Long id) {
        return requestRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<ModificationRequest> searchRequests(RequestStatus status, String search, Pageable pageable) {
        return requestRepository.searchRequests(status, search, pageable);
    }

    // ========== Dashboard Analytics ==========

    @Transactional(readOnly = true)
    public AdminDashboardDto buildDashboardData(UserService userService, VehicleService vehicleService) {
        AdminDashboardDto dto = new AdminDashboardDto();

        dto.setTotalCustomers(userService.countCustomers());
        dto.setTotalVehicles(vehicleService.countAllVehicles());
        dto.setTotalRequests(requestRepository.count());
        dto.setPendingRequests(requestRepository.countByStatus(RequestStatus.PENDING));
        dto.setApprovedRequests(requestRepository.countByStatus(RequestStatus.APPROVED));
        dto.setInProgressRequests(requestRepository.countByStatus(RequestStatus.IN_PROGRESS));
        dto.setCompletedRequests(requestRepository.countByStatus(RequestStatus.COMPLETED));
        dto.setRejectedRequests(requestRepository.countByStatus(RequestStatus.REJECTED));
        dto.setTotalRevenue(requestRepository.calculateTotalRevenue());

        // Monthly data (current year)
        int currentYear = LocalDateTime.now().getYear();
        List<Object[]> monthlyData = requestRepository.countByMonth(currentYear);
        List<Integer> monthlyCounts = new ArrayList<>(Collections.nCopies(12, 0));
        for (Object[] row : monthlyData) {
            int month = ((Number) row[0]).intValue() - 1; // 0-indexed
            long count = ((Number) row[1]).longValue();
            monthlyCounts.set(month, (int) count);
        }
        dto.setMonthlyRequestCounts(monthlyCounts);

        // Status breakdown
        Map<String, Long> statusMap = new LinkedHashMap<>();
        for (RequestStatus s : RequestStatus.values()) {
            statusMap.put(s.name(), 0L);
        }
        List<Object[]> statusData = requestRepository.countByStatusGrouped();
        for (Object[] row : statusData) {
            statusMap.put(row[0].toString(), ((Number) row[1]).longValue());
        }
        dto.setRequestsByStatus(statusMap);

        return dto;
    }

    @Transactional(readOnly = true)
    public long countCustomerRequests(Long customerId) {
        return requestRepository.countByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public long countByStatus(RequestStatus status) {
        return requestRepository.countByStatus(status);
    }
}
