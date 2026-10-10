package com.carmod.app.repository;

import com.carmod.app.entity.ModificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModificationServiceRepository extends JpaRepository<ModificationService, Long> {

    List<ModificationService> findByCategoryIdAndAvailableTrue(Long categoryId);

    List<ModificationService> findByAvailableTrue();

    Page<ModificationService> findAll(Pageable pageable);

    @Query("SELECT s FROM ModificationService s WHERE s.available = true AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ModificationService> searchAvailableServices(@Param("search") String search, Pageable pageable);

    @Query("SELECT s FROM ModificationService s WHERE " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ModificationService> searchAllServices(@Param("search") String search, Pageable pageable);

    Page<ModificationService> findByCategoryId(Long categoryId, Pageable pageable);

    List<ModificationService> findByCategoryId(Long categoryId);
}
