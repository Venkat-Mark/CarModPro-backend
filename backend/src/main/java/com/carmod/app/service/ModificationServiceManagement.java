package com.carmod.app.service;

import com.carmod.app.entity.ModificationCategory;
import com.carmod.app.entity.ModificationService;
import com.carmod.app.repository.ModificationCategoryRepository;
import com.carmod.app.repository.ModificationServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ModificationServiceManagement {

    @Autowired private ModificationServiceRepository serviceRepository;
    @Autowired private ModificationCategoryRepository categoryRepository;

    // ========== Category Operations ==========

    public ModificationCategory saveCategory(ModificationCategory category) {
        if (category.getId() == null && categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("A category with this name already exists.");
        }
        if (category.getId() != null &&
            categoryRepository.existsByNameAndIdNot(category.getName(), category.getId())) {
            throw new IllegalArgumentException("A category with this name already exists.");
        }
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<ModificationCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ModificationCategory> getActiveCategories() {
        return categoryRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Optional<ModificationCategory> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public void toggleCategoryStatus(Long id) {
        ModificationCategory cat = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        cat.setActive(!cat.isActive());
        categoryRepository.save(cat);
    }

    // ========== Service Operations ==========

    public ModificationService saveService(ModificationService service) {
        return serviceRepository.save(service);
    }

    @Transactional(readOnly = true)
    public Page<ModificationService> getAllServices(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return serviceRepository.searchAllServices(search.trim(), pageable);
        }
        return serviceRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ModificationService> getServicesByCategory(Long categoryId, Pageable pageable) {
        return serviceRepository.findByCategoryId(categoryId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ModificationService> getAvailableServices(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return serviceRepository.searchAvailableServices(search.trim(), pageable);
        }
        return serviceRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<ModificationService> getAvailableServicesByCategory(Long categoryId) {
        return serviceRepository.findByCategoryIdAndAvailableTrue(categoryId);
    }

    @Transactional(readOnly = true)
    public List<ModificationService> getAllAvailableServices() {
        return serviceRepository.findByAvailableTrue();
    }

    @Transactional(readOnly = true)
    public Optional<ModificationService> findServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    public void toggleServiceAvailability(Long id) {
        ModificationService service = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        service.setAvailable(!service.isAvailable());
        serviceRepository.save(service);
    }

    @Transactional(readOnly = true)
    public List<ModificationService> getServicesByIds(List<Long> ids) {
        return serviceRepository.findAllById(ids);
    }
}
