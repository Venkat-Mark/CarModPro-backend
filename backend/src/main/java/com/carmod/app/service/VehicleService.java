package com.carmod.app.service;

import com.carmod.app.entity.User;
import com.carmod.app.entity.Vehicle;
import com.carmod.app.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleService {

    @Autowired private VehicleRepository vehicleRepository;

    public Vehicle addVehicle(Vehicle vehicle) {
        if (vehicleRepository.existsByRegistrationNumber(vehicle.getRegistrationNumber())) {
            throw new IllegalArgumentException("A vehicle with registration number '" +
                    vehicle.getRegistrationNumber() + "' already exists.");
        }
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long vehicleId, Vehicle updatedData, Long ownerId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        // Security check: ensure owner matches
        if (!vehicle.getOwner().getId().equals(ownerId)) {
            throw new SecurityException("Access denied: you do not own this vehicle");
        }

        // Check uniqueness if reg number changed
        if (!vehicle.getRegistrationNumber().equals(updatedData.getRegistrationNumber()) &&
            vehicleRepository.existsByRegistrationNumberAndIdNot(updatedData.getRegistrationNumber(), vehicleId)) {
            throw new IllegalArgumentException("Registration number already exists.");
        }

        vehicle.setRegistrationNumber(updatedData.getRegistrationNumber());
        vehicle.setBrand(updatedData.getBrand());
        vehicle.setModel(updatedData.getModel());
        vehicle.setYear(updatedData.getYear());
        vehicle.setFuelType(updatedData.getFuelType());
        vehicle.setColor(updatedData.getColor());
        vehicle.setTransmission(updatedData.getTransmission());

        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long vehicleId, Long ownerId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        if (!vehicle.getOwner().getId().equals(ownerId)) {
            throw new SecurityException("Access denied: you do not own this vehicle");
        }

        vehicleRepository.delete(vehicle);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByOwner(User owner) {
        return vehicleRepository.findByOwner(owner);
    }

    @Transactional(readOnly = true)
    public Optional<Vehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Vehicle> findByIdAndOwner(Long vehicleId, Long ownerId) {
        return vehicleRepository.findById(vehicleId)
                .filter(v -> v.getOwner().getId().equals(ownerId));
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> getAllVehicles(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public long countAllVehicles() {
        return vehicleRepository.countAllVehicles();
    }

    @Transactional(readOnly = true)
    public List<Vehicle> searchVehiclesByOwner(Long ownerId, String search) {
        if (search == null || search.isBlank()) {
            return vehicleRepository.findByOwnerId(ownerId);
        }
        return vehicleRepository.searchByOwner(ownerId, search.trim());
    }
}
