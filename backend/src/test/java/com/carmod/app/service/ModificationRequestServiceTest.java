package com.carmod.app.service;

import com.carmod.app.dto.ModificationRequestDto;
import com.carmod.app.dto.RequestStatusUpdateDto;
import com.carmod.app.entity.ModificationRequest;
import com.carmod.app.entity.ModificationRequest.RequestStatus;
import com.carmod.app.entity.ModificationService;
import com.carmod.app.entity.RequestItem;
import com.carmod.app.entity.User;
import com.carmod.app.entity.Vehicle;
import com.carmod.app.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModificationRequestServiceTest {

    @Mock private ModificationRequestRepository requestRepository;
    @Mock private RequestItemRepository requestItemRepository;
    @Mock private ModificationServiceRepository serviceRepository;
    @Mock private VehicleRepository vehicleRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ModificationRequestService requestService;

    private User customer;
    private Vehicle vehicle;
    private ModificationService service1;
    private ModificationService service2;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setId(10L);
        customer.setFullName("Car Enthusiast");
        customer.setEmail("enthusiast@example.com");

        vehicle = new Vehicle();
        vehicle.setId(20L);
        vehicle.setOwner(customer);
        vehicle.setRegistrationNumber("TN07AB1234");
        vehicle.setBrand("BMW");
        vehicle.setModel("330i");

        service1 = new ModificationService();
        service1.setId(101L);
        service1.setName("Cold Air Intake");
        service1.setBasePrice(new BigDecimal("15000.00"));

        service2 = new ModificationService();
        service2.setId(102L);
        service2.setName("Carbon Fiber Spoiler");
        service2.setBasePrice(new BigDecimal("25000.00"));
    }

    @Test
    @DisplayName("Should successfully calculate price estimate for multiple services")
    void calculateEstimate_Success() {
        when(serviceRepository.findAllById(List.of(101L, 102L))).thenReturn(List.of(service1, service2));

        BigDecimal estimate = requestService.calculateEstimate(List.of(101L, 102L));

        assertEquals(new BigDecimal("40000.00"), estimate);
    }

    @Test
    @DisplayName("Should successfully create a modification request with line items")
    void createRequest_Success() {
        ModificationRequestDto dto = new ModificationRequestDto();
        dto.setVehicleId(20L);
        dto.setServiceIds(List.of(101L, 102L));
        dto.setCustomerNotes("Please take extra care with paint.");

        when(userRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findById(20L)).thenReturn(Optional.of(vehicle));
        when(serviceRepository.findAllById(List.of(101L, 102L))).thenReturn(List.of(service1, service2));

        when(requestRepository.save(any(ModificationRequest.class))).thenAnswer(invocation -> {
            ModificationRequest r = invocation.getArgument(0);
            r.setId(1001L);
            return r;
        });

        ModificationRequest created = requestService.createRequest(dto, 10L);

        assertNotNull(created);
        assertEquals(1001L, created.getId());
        assertEquals(RequestStatus.PENDING, created.getStatus());
        assertEquals(new BigDecimal("40000.00"), created.getEstimatedCost());
        assertEquals("Please take extra care with paint.", created.getCustomerNotes());

        verify(requestItemRepository, times(2)).save(any(RequestItem.class));
    }

    @Test
    @DisplayName("Should deny request creation if vehicle does not belong to the user")
    void createRequest_VehicleOwnershipMismatch_ThrowsSecurityException() {
        User differentOwner = new User();
        differentOwner.setId(99L);
        vehicle.setOwner(differentOwner);

        ModificationRequestDto dto = new ModificationRequestDto();
        dto.setVehicleId(20L);
        dto.setServiceIds(List.of(101L));

        when(userRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findById(20L)).thenReturn(Optional.of(vehicle));

        assertThrows(SecurityException.class, () -> {
            requestService.createRequest(dto, 10L);
        });

        verify(requestRepository, never()).save(any(ModificationRequest.class));
    }

    @Test
    @DisplayName("Should allow customer to cancel a PENDING request")
    void cancelRequest_PendingStatus_Success() {
        ModificationRequest request = new ModificationRequest();
        request.setId(500L);
        request.setCustomer(customer);
        request.setStatus(RequestStatus.PENDING);

        when(requestRepository.findByIdAndCustomerId(500L, 10L)).thenReturn(Optional.of(request));

        requestService.cancelRequest(500L, 10L);

        assertEquals(RequestStatus.CANCELLED, request.getStatus());
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    @DisplayName("Should disallow cancellation if request is already IN_PROGRESS or COMPLETED")
    void cancelRequest_InProgressStatus_ThrowsIllegalStateException() {
        ModificationRequest request = new ModificationRequest();
        request.setId(500L);
        request.setCustomer(customer);
        request.setStatus(RequestStatus.IN_PROGRESS);

        when(requestRepository.findByIdAndCustomerId(500L, 10L)).thenReturn(Optional.of(request));

        assertThrows(IllegalStateException.class, () -> {
            requestService.cancelRequest(500L, 10L);
        });

        verify(requestRepository, never()).save(request);
    }

    @Test
    @DisplayName("Admin can update request status and final cost")
    void updateStatus_AdminAction_Success() {
        ModificationRequest request = new ModificationRequest();
        request.setId(700L);
        request.setStatus(RequestStatus.PENDING);
        request.setEstimatedCost(new BigDecimal("15000.00"));

        when(requestRepository.findById(700L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(ModificationRequest.class))).thenAnswer(i -> i.getArgument(0));

        RequestStatusUpdateDto updateDto = new RequestStatusUpdateDto();
        updateDto.setRequestId(700L);
        updateDto.setStatus(RequestStatus.COMPLETED);
        updateDto.setFinalCost(new BigDecimal("16500.00"));
        updateDto.setAdminNotes("Completed with high temp finish.");

        ModificationRequest updated = requestService.updateStatus(updateDto);

        assertEquals(RequestStatus.COMPLETED, updated.getStatus());
        assertEquals(new BigDecimal("16500.00"), updated.getFinalCost());
        assertEquals("Completed with high temp finish.", updated.getAdminNotes());
        assertNotNull(updated.getCompletedAt());
    }
}
