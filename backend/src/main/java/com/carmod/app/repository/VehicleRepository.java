package com.carmod.app.repository;

import com.carmod.app.entity.Vehicle;
import com.carmod.app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByOwner(User owner);

    List<Vehicle> findByOwnerId(Long ownerId);

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumberAndIdNot(String registrationNumber, Long id);

    @Query("SELECT v FROM Vehicle v WHERE v.owner.id = :ownerId AND " +
           "(LOWER(v.brand) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(v.model) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(v.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Vehicle> searchByOwner(@Param("ownerId") Long ownerId, @Param("search") String search);

    @Query("SELECT COUNT(v) FROM Vehicle v")
    long countAllVehicles();

    Page<Vehicle> findAll(Pageable pageable);
}
