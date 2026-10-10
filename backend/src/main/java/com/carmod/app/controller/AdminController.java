package com.carmod.app.controller;

import com.carmod.app.dto.AdminDashboardDto;
import com.carmod.app.dto.RequestStatusUpdateDto;
import com.carmod.app.entity.*;
import com.carmod.app.entity.ModificationRequest.RequestStatus;
import com.carmod.app.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private VehicleService vehicleService;
    @Autowired private ModificationRequestService requestService;
    @Autowired private ModificationServiceManagement serviceManagement;
    @Autowired private FeedbackService feedbackService;

    // ========== Dashboard ==========

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        AdminDashboardDto dashboardData = requestService.buildDashboardData(userService, vehicleService);
        model.addAttribute("dashboard", dashboardData);
        return "admin/dashboard";
    }

    // ========== Customers ==========

    @GetMapping("/customers")
    public String customers(@RequestParam(required = false) String search,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {
        Page<User> customers = userService.getAllCustomers(search, PageRequest.of(page, size,
                Sort.by("createdAt").descending()));
        model.addAttribute("customers", customers);
        model.addAttribute("searchQuery", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customers.getTotalPages());
        return "admin/customers";
    }

    @GetMapping("/customers/{id}")
    public String customerDetail(@PathVariable Long id, Model model) {
        return userService.findById(id).map(customer -> {
            List<Vehicle> vehicles = vehicleService.getVehiclesByOwner(customer);
            Page<ModificationRequest> requests = requestService.getCustomerRequests(
                    id, PageRequest.of(0, 10, Sort.by("requestDate").descending()));
            model.addAttribute("customer", customer);
            model.addAttribute("vehicles", vehicles);
            model.addAttribute("requests", requests.getContent());
            return "admin/customer-detail";
        }).orElse("redirect:/admin/customers");
    }

    @PostMapping("/customers/{id}/toggle-status")
    public String toggleCustomerStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Customer status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/customers";
    }

    // ========== Vehicles ==========

    @GetMapping("/vehicles")
    public String vehicles(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        Page<Vehicle> vehicles = vehicleService.getAllVehicles(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vehicles.getTotalPages());
        return "admin/vehicles";
    }

    // ========== Categories ==========

    @GetMapping("/categories")
    public String categories(Model model) {
        List<ModificationCategory> categories = serviceManagement.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("newCategory", new ModificationCategory());
        return "admin/categories";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@Valid @ModelAttribute("newCategory") ModificationCategory category,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", serviceManagement.getAllCategories());
            return "admin/categories";
        }
        try {
            serviceManagement.saveCategory(category);
            redirectAttributes.addFlashAttribute("successMessage",
                    category.getId() == null ? "Category created!" : "Category updated!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryPage(@PathVariable Long id, Model model) {
        return serviceManagement.findCategoryById(id).map(category -> {
            model.addAttribute("categories", serviceManagement.getAllCategories());
            model.addAttribute("newCategory", category);
            model.addAttribute("editMode", true);
            return "admin/categories";
        }).orElse("redirect:/admin/categories");
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceManagement.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cannot delete category with existing services.");
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/toggle")
    public String toggleCategoryStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        serviceManagement.toggleCategoryStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Category status updated.");
        return "redirect:/admin/categories";
    }

    // ========== Services ==========

    @GetMapping("/services")
    public String services(@RequestParam(required = false) String search,
                           @RequestParam(required = false) Long category,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        Page<ModificationService> services;
        if (category != null) {
            services = serviceManagement.getServicesByCategory(category, PageRequest.of(page, size));
        } else {
            services = serviceManagement.getAllServices(search, PageRequest.of(page, size));
        }
        model.addAttribute("services", services);
        model.addAttribute("categories", serviceManagement.getAllCategories());
        model.addAttribute("searchQuery", search);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", services.getTotalPages());
        return "admin/services";
    }

    @GetMapping("/services/new")
    public String newServicePage(Model model) {
        model.addAttribute("service", new ModificationService());
        model.addAttribute("categories", serviceManagement.getActiveCategories());
        model.addAttribute("pageTitle", "Add New Service");
        model.addAttribute("isEdit", false);
        return "admin/service-form";
    }

    @PostMapping("/services/save")
    public String saveService(@Valid @ModelAttribute("service") ModificationService service,
                              BindingResult result,
                              @RequestParam Long categoryId,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", serviceManagement.getActiveCategories());
            model.addAttribute("pageTitle", service.getId() == null ? "Add New Service" : "Edit Service");
            model.addAttribute("isEdit", service.getId() != null);
            return "admin/service-form";
        }
        try {
            ModificationCategory category = serviceManagement.findCategoryById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            service.setCategory(category);
            serviceManagement.saveService(service);
            redirectAttributes.addFlashAttribute("successMessage",
                    service.getId() == null ? "Service created!" : "Service updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @GetMapping("/services/edit/{id}")
    public String editServicePage(@PathVariable Long id, Model model) {
        return serviceManagement.findServiceById(id).map(service -> {
            model.addAttribute("service", service);
            model.addAttribute("categories", serviceManagement.getActiveCategories());
            model.addAttribute("pageTitle", "Edit Service");
            model.addAttribute("isEdit", true);
            return "admin/service-form";
        }).orElse("redirect:/admin/services");
    }

    @PostMapping("/services/delete/{id}")
    public String deleteService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceManagement.deleteService(id);
            redirectAttributes.addFlashAttribute("successMessage", "Service deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete service with existing orders.");
        }
        return "redirect:/admin/services";
    }

    @PostMapping("/services/{id}/toggle")
    public String toggleServiceAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        serviceManagement.toggleServiceAvailability(id);
        redirectAttributes.addFlashAttribute("successMessage", "Service availability updated.");
        return "redirect:/admin/services";
    }

    // ========== Modification Requests ==========

    @GetMapping("/requests")
    public String requests(@RequestParam(required = false) String status,
                           @RequestParam(required = false) String search,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        RequestStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = RequestStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        Page<ModificationRequest> requests = requestService.searchRequests(
                statusEnum, search, PageRequest.of(page, size, Sort.by("requestDate").descending()));

        model.addAttribute("requests", requests);
        model.addAttribute("statusFilter", status);
        model.addAttribute("searchQuery", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requests.getTotalPages());
        model.addAttribute("statuses", RequestStatus.values());
        return "admin/requests";
    }

    @GetMapping("/requests/{id}")
    public String requestDetail(@PathVariable Long id, Model model) {
        return requestService.findById(id).map(request -> {
            model.addAttribute("request", request);
            model.addAttribute("updateDto", new RequestStatusUpdateDto());
            model.addAttribute("statuses", RequestStatus.values());
            feedbackService.findByRequestId(id).ifPresent(fb -> model.addAttribute("feedback", fb));
            return "admin/request-detail";
        }).orElse("redirect:/admin/requests");
    }

    @PostMapping("/requests/{id}/update-status")
    public String updateRequestStatus(@PathVariable Long id,
                                      @ModelAttribute RequestStatusUpdateDto dto,
                                      RedirectAttributes redirectAttributes) {
        dto.setRequestId(id);
        try {
            requestService.updateStatus(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Request status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/requests/" + id;
    }

    // ========== Feedback ==========

    @GetMapping("/feedback")
    public String feedback(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        Page<Feedback> feedbacks = feedbackService.getAllFeedbacks(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        Double avgRating = feedbackService.getAverageRating();
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("avgRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", feedbacks.getTotalPages());
        return "admin/feedback";
    }

    @PostMapping("/feedback/delete/{id}")
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            feedbackService.deleteFeedback(id);
            redirectAttributes.addFlashAttribute("successMessage", "Feedback removed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/feedback";
    }
}
