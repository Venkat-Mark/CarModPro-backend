package com.carmod.app.controller;

import com.carmod.app.dto.ModificationRequestDto;
import com.carmod.app.dto.UserProfileDto;
import com.carmod.app.entity.*;
import com.carmod.app.entity.ModificationRequest.RequestStatus;
import com.carmod.app.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired private UserService userService;
    @Autowired private VehicleService vehicleService;
    @Autowired private ModificationRequestService requestService;
    @Autowired private ModificationServiceManagement serviceManagement;
    @Autowired private FeedbackService feedbackService;

    private User getCurrentUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    // ========== Dashboard ==========

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getCurrentUser(userDetails);
        long totalRequests = requestService.countCustomerRequests(user.getId());
        long pendingRequests = requestService.countCustomerRequests(user.getId()); // refined below
        List<Vehicle> vehicles = vehicleService.getVehiclesByOwner(user);

        Page<ModificationRequest> recentRequests = requestService.getCustomerRequests(
                user.getId(), PageRequest.of(0, 5, Sort.by("requestDate").descending()));

        long pending = recentRequests.getContent().stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING).count();
        long completed = recentRequests.getContent().stream()
                .filter(r -> r.getStatus() == RequestStatus.COMPLETED).count();

        model.addAttribute("user", user);
        model.addAttribute("vehicleCount", vehicles.size());
        model.addAttribute("totalRequests", recentRequests.getTotalElements());
        model.addAttribute("recentRequests", recentRequests.getContent());
        model.addAttribute("vehicles", vehicles);
        return "customer/dashboard";
    }

    // ========== Profile ==========

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getCurrentUser(userDetails);
        model.addAttribute("profileDto", userService.toProfileDto(user));
        model.addAttribute("user", user);
        return "customer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @Valid @ModelAttribute("profileDto") UserProfileDto dto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        User user = getCurrentUser(userDetails);
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "customer/profile";
        }
        try {
            userService.updateProfile(user.getId(), dto);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/customer/profile";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);
            return "customer/profile";
        }
    }

    // ========== Vehicles ==========

    @GetMapping("/vehicles")
    public String vehicles(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam(required = false) String search,
                           Model model) {
        User user = getCurrentUser(userDetails);
        List<Vehicle> vehicles = vehicleService.searchVehiclesByOwner(user.getId(), search);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("user", user);
        model.addAttribute("searchQuery", search);
        return "customer/vehicles";
    }

    @GetMapping("/vehicles/add")
    public String addVehiclePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getCurrentUser(userDetails);
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Add Vehicle");
        model.addAttribute("isEdit", false);
        return "customer/vehicle-form";
    }

    @PostMapping("/vehicles/add")
    public String addVehicle(@AuthenticationPrincipal UserDetails userDetails,
                             @Valid @ModelAttribute("vehicle") Vehicle vehicle,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        User user = getCurrentUser(userDetails);
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Add Vehicle");
            model.addAttribute("isEdit", false);
            return "customer/vehicle-form";
        }
        try {
            vehicle.setOwner(user);
            vehicleService.addVehicle(vehicle);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle added successfully!");
            return "redirect:/customer/vehicles";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Add Vehicle");
            model.addAttribute("isEdit", false);
            return "customer/vehicle-form";
        }
    }

    @GetMapping("/vehicles/edit/{id}")
    public String editVehiclePage(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        User user = getCurrentUser(userDetails);
        return vehicleService.findByIdAndOwner(id, user.getId())
                .map(vehicle -> {
                    model.addAttribute("vehicle", vehicle);
                    model.addAttribute("user", user);
                    model.addAttribute("pageTitle", "Edit Vehicle");
                    model.addAttribute("isEdit", true);
                    return "customer/vehicle-form";
                })
                .orElse("redirect:/customer/vehicles");
    }

    @PostMapping("/vehicles/edit/{id}")
    public String editVehicle(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              @Valid @ModelAttribute("vehicle") Vehicle vehicle,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        User user = getCurrentUser(userDetails);
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit Vehicle");
            model.addAttribute("isEdit", true);
            return "customer/vehicle-form";
        }
        try {
            vehicleService.updateVehicle(id, vehicle, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle updated successfully!");
            return "redirect:/customer/vehicles";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit Vehicle");
            model.addAttribute("isEdit", true);
            return "customer/vehicle-form";
        }
    }

    @PostMapping("/vehicles/delete/{id}")
    public String deleteVehicle(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(userDetails);
        try {
            vehicleService.deleteVehicle(id, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/customer/vehicles";
    }

    // ========== Modification Requests ==========

    @GetMapping("/requests")
    public String requests(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        User user = getCurrentUser(userDetails);
        Page<ModificationRequest> requests = requestService.getCustomerRequests(
                user.getId(), PageRequest.of(page, size, Sort.by("requestDate").descending()));
        model.addAttribute("requests", requests);
        model.addAttribute("user", user);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requests.getTotalPages());
        return "customer/requests";
    }

    @GetMapping("/requests/new")
    public String newRequestPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getCurrentUser(userDetails);
        List<Vehicle> vehicles = vehicleService.getVehiclesByOwner(user);
        if (vehicles.isEmpty()) {
            model.addAttribute("errorMessage", "Please add a vehicle before creating a modification request.");
            return "redirect:/customer/vehicles";
        }
        List<com.carmod.app.entity.ModificationCategory> categories = serviceManagement.getActiveCategories();
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("categories", categories);
        model.addAttribute("requestDto", new ModificationRequestDto());
        model.addAttribute("user", user);
        return "customer/request-new";
    }

    @PostMapping("/requests/new")
    public String createRequest(@AuthenticationPrincipal UserDetails userDetails,
                                @Valid @ModelAttribute("requestDto") ModificationRequestDto dto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        User user = getCurrentUser(userDetails);
        if (result.hasErrors()) {
            List<Vehicle> vehicles = vehicleService.getVehiclesByOwner(user);
            model.addAttribute("vehicles", vehicles);
            model.addAttribute("categories", serviceManagement.getActiveCategories());
            model.addAttribute("user", user);
            return "customer/request-new";
        }
        try {
            ModificationRequest request = requestService.createRequest(dto, user.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Modification request #" + request.getId() + " submitted successfully!");
            return "redirect:/customer/requests";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("vehicles", vehicleService.getVehiclesByOwner(user));
            model.addAttribute("categories", serviceManagement.getActiveCategories());
            model.addAttribute("user", user);
            return "customer/request-new";
        }
    }

    @GetMapping("/requests/{id}")
    public String requestDetail(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        User user = getCurrentUser(userDetails);
        return requestService.findByIdAndCustomer(id, user.getId())
                .map(request -> {
                    model.addAttribute("request", request);
                    model.addAttribute("user", user);
                    boolean hasFeedback = feedbackService.findByRequestId(id).isPresent();
                    model.addAttribute("hasFeedback", hasFeedback);
                    return "customer/request-detail";
                })
                .orElse("redirect:/customer/requests");
    }

    @PostMapping("/requests/{id}/cancel")
    public String cancelRequest(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(userDetails);
        try {
            requestService.cancelRequest(id, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Request cancelled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/customer/requests";
    }

    // ========== Feedback ==========

    @GetMapping("/requests/{id}/feedback")
    public String feedbackPage(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        User user = getCurrentUser(userDetails);
        return requestService.findByIdAndCustomer(id, user.getId())
                .filter(r -> r.getStatus() == RequestStatus.COMPLETED)
                .map(request -> {
                    if (feedbackService.findByRequestId(id).isPresent()) {
                        model.addAttribute("errorMessage", "You have already submitted feedback for this request.");
                        return "redirect:/customer/requests/" + id;
                    }
                    model.addAttribute("request", request);
                    model.addAttribute("user", user);
                    return "customer/feedback-form";
                })
                .orElse("redirect:/customer/requests");
    }

    @PostMapping("/requests/{id}/feedback")
    public String submitFeedback(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam Integer rating,
                                 @RequestParam(required = false) String comments,
                                 RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(userDetails);
        try {
            feedbackService.submitFeedback(id, user.getId(), rating, comments);
            redirectAttributes.addFlashAttribute("successMessage", "Thank you for your feedback!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/customer/requests/" + id;
    }

    // ========== AJAX endpoint for cost estimate ==========

    @GetMapping("/api/estimate")
    @ResponseBody
    public BigDecimal getEstimate(@RequestParam List<Long> serviceIds) {
        return requestService.calculateEstimate(serviceIds);
    }
}
