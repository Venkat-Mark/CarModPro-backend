package com.carmod.app.repository;

import com.carmod.app.entity.ModificationRequest;
import com.carmod.app.entity.ModificationRequest.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModificationRequestRepository extends JpaRepository<ModificationRequest, Long> {

    Page<ModificationRequest> findByCustomerId(Long customerId, Pageable pageable);

    List<ModificationRequest> findByCustomerIdOrderByRequestDateDesc(Long customerId);

    Page<ModificationRequest> findByStatus(RequestStatus status, Pageable pageable);

    long countByStatus(RequestStatus status);

    long countByCustomerId(Long customerId);

    Optional<ModificationRequest> findByIdAndCustomerId(Long id, Long customerId);

    @Query("SELECT r FROM ModificationRequest r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:search IS NULL OR LOWER(r.customer.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(r.vehicle.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(r.vehicle.brand) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ModificationRequest> searchRequests(
            @Param("status") RequestStatus status,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(r.finalCost), 0) FROM ModificationRequest r WHERE r.status = 'COMPLETED'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT MONTH(r.requestDate) as month, COUNT(r) as count " +
           "FROM ModificationRequest r " +
           "WHERE YEAR(r.requestDate) = :year " +
           "GROUP BY MONTH(r.requestDate) ORDER BY month")
    List<Object[]> countByMonth(@Param("year") int year);

    @Query("SELECT r.status as status, COUNT(r) as count FROM ModificationRequest r GROUP BY r.status")
    List<Object[]> countByStatusGrouped();
}
