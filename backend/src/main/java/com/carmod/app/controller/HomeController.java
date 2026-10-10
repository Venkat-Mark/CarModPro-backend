package com.carmod.app.controller;

import com.carmod.app.entity.ModificationCategory;
import com.carmod.app.entity.ModificationService;
import com.carmod.app.service.ModificationServiceManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {

    @Autowired private ModificationServiceManagement serviceManagement;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<ModificationCategory> categories = serviceManagement.getActiveCategories();
        // Get a few featured services for home page
        Page<ModificationService> featuredServices = serviceManagement.getAvailableServices(null,
                PageRequest.of(0, 6));
        model.addAttribute("categories", categories);
        model.addAttribute("featuredServices", featuredServices.getContent());
        return "home";
    }

    @GetMapping("/services")
    public String services(
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Page<ModificationService> services;
        if (category != null) {
            services = serviceManagement.getServicesByCategory(category, PageRequest.of(page, size));
        } else {
            services = serviceManagement.getAvailableServices(search, PageRequest.of(page, size));
        }

        List<ModificationCategory> categories = serviceManagement.getActiveCategories();
        model.addAttribute("services", services);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchQuery", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", services.getTotalPages());
        return "services";
    }

    @GetMapping("/services/{id}")
    public String serviceDetail(@PathVariable Long id, Model model) {
        return serviceManagement.findServiceById(id)
                .map(service -> {
                    model.addAttribute("service", service);
                    // Related services in same category
                    List<ModificationService> related = serviceManagement
                            .getAvailableServicesByCategory(service.getCategory().getId());
                    related.removeIf(s -> s.getId().equals(id));
                    if (related.size() > 4) related = related.subList(0, 4);
                    model.addAttribute("relatedServices", related);
                    return "service-detail";
                })
                .orElse("redirect:/services");
    }
}
