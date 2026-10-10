package com.carmod.app.config;

import com.carmod.app.entity.*;
import com.carmod.app.entity.ModificationRequest.RequestStatus;
import com.carmod.app.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private UserRepository userRepository;
    @Autowired private ModificationCategoryRepository categoryRepository;
    @Autowired private ModificationServiceRepository serviceRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private ModificationRequestRepository requestRepository;
    @Autowired private RequestItemRepository requestItemRepository;
    @Autowired private FeedbackRepository feedbackRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@carmod.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@1234}")
    private String adminPassword;

    @Value("${app.admin.name:System Administrator}")
    private String adminName;

    @Override
    @Transactional
    public void run(String... args) {
        initializeAdmin();
        initializeCategories();
        initializeSampleData();
    }

    private void initializeAdmin() {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setFullName(adminName);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(User.Role.ADMIN);
            admin.setStatus(User.AccountStatus.ACTIVE);
            admin.setPhone("+91-9000000000");
            userRepository.save(admin);
            logger.info("Admin account created: {}", adminEmail);
        }
    }

    private void initializeCategories() {
        if (categoryRepository.count() > 0) return;

        createCategoryWithServices(
            "Exterior Modifications",
            "Transform your car's exterior look with premium body kits, wraps, and paint",
            "bi-car-front",
            List.of(
                new ServiceData("Full Body Paint Job", "Premium automotive paint with UV protection and glossy finish", new BigDecimal("25000"), "3-5 days"),
                new ServiceData("Carbon Fiber Hood", "Lightweight carbon fiber hood replacement for sporty look", new BigDecimal("18000"), "1-2 days"),
                new ServiceData("Body Kit Installation", "Complete aerodynamic body kit with front/rear bumpers and side skirts", new BigDecimal("35000"), "3-4 days"),
                new ServiceData("Vinyl Wrap - Full Car", "Premium vinyl wrap with color change, matte or gloss finish", new BigDecimal("22000"), "2-3 days"),
                new ServiceData("Roof Spoiler", "Aerodynamic roof spoiler for added downforce and style", new BigDecimal("8000"), "1 day")
            )
        );

        createCategoryWithServices(
            "Interior Modifications",
            "Upgrade your driving experience with premium interior customizations",
            "bi-layout-text-sidebar-reverse",
            List.of(
                new ServiceData("Leather Seat Upholstery", "Full custom leather seat covers with stitching options", new BigDecimal("30000"), "2-3 days"),
                new ServiceData("Dashboard Customization", "Carbon fiber or wood grain dashboard trim kit", new BigDecimal("15000"), "1-2 days"),
                new ServiceData("Floor Mats - Premium", "Custom-fit 3D floor mats with anti-slip backing", new BigDecimal("5000"), "Same day"),
                new ServiceData("Interior LED Ambient Lighting", "Multi-color RGB ambient lighting throughout the cabin", new BigDecimal("8000"), "1 day"),
                new ServiceData("Steering Wheel Replacement", "Sports steering wheel with flat bottom and paddle shifters", new BigDecimal("12000"), "1 day")
            )
        );

        createCategoryWithServices(
            "Alloy Wheels & Tyres",
            "Enhance performance and aesthetics with premium wheel and tyre packages",
            "bi-circle",
            List.of(
                new ServiceData("Alloy Wheels - 17 inch Set", "Premium alloy wheels, set of 4 with mounting", new BigDecimal("40000"), "Same day"),
                new ServiceData("Alloy Wheels - 18 inch Set", "Large diameter premium alloy wheels, set of 4", new BigDecimal("55000"), "Same day"),
                new ServiceData("Performance Tyres - Set of 4", "High-performance low-profile tyres with installation", new BigDecimal("28000"), "Same day"),
                new ServiceData("Wheel Spacers Installation", "Track-width spacers for improved cornering stability", new BigDecimal("6000"), "Same day"),
                new ServiceData("Chrome Lug Nuts Set", "Premium chrome lug nuts, set of 20 with torque fitting", new BigDecimal("2000"), "Same day")
            )
        );

        createCategoryWithServices(
            "Lighting Upgrades",
            "Improve visibility and aesthetics with modern lighting solutions",
            "bi-lightbulb",
            List.of(
                new ServiceData("LED Headlight Conversion", "High-intensity LED headlights replacing halogen units", new BigDecimal("12000"), "1 day"),
                new ServiceData("LED DRL Kit", "Daytime running lights integration with factory look", new BigDecimal("6000"), "1 day"),
                new ServiceData("HID Xenon Kit", "High-intensity discharge xenon lighting system", new BigDecimal("9000"), "1 day"),
                new ServiceData("LED Tail Light Upgrade", "Full LED tail light assembly replacement", new BigDecimal("8000"), "1 day"),
                new ServiceData("Underglow LED Kit", "Exterior RGB underglow lighting system with remote", new BigDecimal("7000"), "1 day")
            )
        );

        createCategoryWithServices(
            "Audio & Infotainment",
            "Upgrade your in-car entertainment with premium audio and infotainment systems",
            "bi-speaker",
            List.of(
                new ServiceData("Android Head Unit", "10-inch Android touchscreen with GPS, Bluetooth, WiFi", new BigDecimal("18000"), "1 day"),
                new ServiceData("Premium Speaker Set", "Component speakers with tweeters, front and rear", new BigDecimal("15000"), "1 day"),
                new ServiceData("Subwoofer & Amplifier", "1000W amplifier with 12-inch subwoofer and enclosure", new BigDecimal("25000"), "1-2 days"),
                new ServiceData("Reverse Camera", "High-resolution reverse camera with parking guidelines", new BigDecimal("4000"), "Same day"),
                new ServiceData("Sound Deadening Kit", "Complete car sound insulation for quiet cabin", new BigDecimal("12000"), "2 days")
            )
        );

        createCategoryWithServices(
            "Performance & Accessories",
            "Enhance your vehicle's performance with professional tuning and accessories",
            "bi-speedometer2",
            List.of(
                new ServiceData("Cold Air Intake", "High-flow cold air intake for improved throttle response", new BigDecimal("8000"), "1 day"),
                new ServiceData("Performance Exhaust System", "Stainless steel cat-back exhaust with resonator delete", new BigDecimal("22000"), "1-2 days"),
                new ServiceData("Suspension Lowering Springs", "Sport lowering springs for improved handling", new BigDecimal("15000"), "1 day"),
                new ServiceData("Window Tinting - Full", "Premium ceramic window tint, all windows", new BigDecimal("8000"), "Same day"),
                new ServiceData("Turbo Timer Installation", "Electronic turbo timer to extend turbocharger life", new BigDecimal("5000"), "Same day")
            )
        );

        logger.info("Categories and 30 services initialized successfully");
    }

    private void createCategoryWithServices(String name, String description, String icon, List<ServiceData> services) {
        ModificationCategory category = new ModificationCategory();
        category.setName(name);
        category.setDescription(description);
        category.setIconClass(icon);
        category.setActive(true);
        category = categoryRepository.save(category);

        for (ServiceData sd : services) {
            ModificationService service = new ModificationService();
            service.setCategory(category);
            service.setName(sd.name);
            service.setDescription(sd.description);
            service.setBasePrice(sd.price);
            service.setDuration(sd.duration);
            service.setAvailable(true);
            serviceRepository.save(service);
        }
    }

    private void initializeSampleData() {
        if (vehicleRepository.count() > 0) return;

        logger.info("Seeding rich sample database records (Customers, Vehicles, Orders, Feedback)...");

        // 1. Create Sample Customers
        User customer1 = createCustomer("Rohit Sharma", "customer@carmod.com", "+91-9876543210");
        User customer2 = createCustomer("Rahul Verma", "rahul.verma@example.com", "+91-9812345678");
        User customer3 = createCustomer("Priya Nair", "priya.nair@example.com", "+91-9765432109");
        User customer4 = createCustomer("Arjun Kapoor", "arjun.kapoor@example.com", "+91-9654321098");

        // 2. Create Sample Vehicles
        Vehicle v1 = createVehicle(customer1, "TN 09 AB 1234", "BMW", "330i M-Sport", 2022, "Petrol", "Midnight Black", "Automatic");
        Vehicle v2 = createVehicle(customer1, "TN 09 CD 5678", "Honda", "City ZX", 2021, "Petrol", "Platinum White", "CVT");
        Vehicle v3 = createVehicle(customer2, "KA 01 EF 9012", "Hyundai", "Creta SX(O)", 2023, "Diesel", "Phantom Black", "Automatic");
        Vehicle v4 = createVehicle(customer3, "MH 02 GH 3456", "Volkswagen", "Polo GT TSI", 2020, "Petrol", "Flash Red", "DCT");
        Vehicle v5 = createVehicle(customer4, "DL 03 JK 7890", "Mahindra", "Thar LX 4x4", 2023, "Diesel", "Napoli Black", "Manual");

        // Fetch services for creating sample line items
        ModificationService exhaust = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Exhaust")).findFirst().orElse(null);
        ModificationService intake = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Intake")).findFirst().orElse(null);
        ModificationService leather = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Leather")).findFirst().orElse(null);
        ModificationService ambient = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Ambient")).findFirst().orElse(null);
        ModificationService wheels18 = serviceRepository.findAll().stream().filter(s -> s.getName().contains("18 inch")).findFirst().orElse(null);
        ModificationService tyres = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Tyres")).findFirst().orElse(null);
        ModificationService paint = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Paint")).findFirst().orElse(null);
        ModificationService ledHead = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Headlight")).findFirst().orElse(null);
        ModificationService android = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Android")).findFirst().orElse(null);
        ModificationService speakers = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Speaker")).findFirst().orElse(null);
        ModificationService bodyKit = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Body Kit")).findFirst().orElse(null);
        ModificationService underglow = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Underglow")).findFirst().orElse(null);
        ModificationService camera = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Camera")).findFirst().orElse(null);
        ModificationService turboTimer = serviceRepository.findAll().stream().filter(s -> s.getName().contains("Turbo")).findFirst().orElse(null);

        LocalDateTime now = LocalDateTime.now();

        // 3. Create Sample Requests & Feedback across various months and statuses

        // Order 1: Completed (Rohit - BMW 330i)
        if (exhaust != null && intake != null) {
            ModificationRequest r1 = createRequest(customer1, v1, RequestStatus.COMPLETED,
                    "Please test exhaust sound note and check engine light prior to delivery.",
                    "Installed with high-temp gaskets. Flawless dyno test.",
                    null,
                    now.minusDays(18), now.minusDays(14),
                    new BigDecimal("30000"), new BigDecimal("32000"),
                    List.of(exhaust, intake));
            createFeedback(customer1, r1, 5, "Incredible exhaust roar and instant throttle response! Top tier workmanship by the CarModPro technicians.");
        }

        // Order 2: Completed (Priya - VW Polo GT)
        if (leather != null && ambient != null) {
            ModificationRequest r2 = createRequest(customer3, v4, RequestStatus.COMPLETED,
                    "Matching red contrast stitching on leather upholstery requested.",
                    "Custom bespoke stitching completed. Ambient lighting integrated to factory switch.",
                    null,
                    now.minusMonths(1).minusDays(5), now.minusMonths(1),
                    new BigDecimal("38000"), new BigDecimal("38000"),
                    List.of(leather, ambient));
            createFeedback(customer3, r2, 5, "The cabin feels like a luxury GT now. The leather fitting and ambient colors are immaculate.");
        }

        // Order 3: Completed (Arjun - Mahindra Thar)
        if (wheels18 != null && tyres != null) {
            ModificationRequest r3 = createRequest(customer4, v5, RequestStatus.COMPLETED,
                    "Require high-speed wheel balancing and alignment with the new 18-inch set.",
                    "Wheel balancing completed to 0g tolerance. Tyre pressures set for all-terrain usage.",
                    null,
                    now.minusMonths(2).minusDays(10), now.minusMonths(2).minusDays(8),
                    new BigDecimal("83000"), new BigDecimal("83000"),
                    List.of(wheels18, tyres));
            createFeedback(customer4, r3, 4, "Stout aggressive stance and rock solid grip on trails. Highly professional installation.");
        }

        // Order 4: In Progress (Rahul - Hyundai Creta)
        if (paint != null && ledHead != null) {
            createRequest(customer2, v3, RequestStatus.IN_PROGRESS,
                    "Ceramic clear coat requested over the new paint finish.",
                    "Vehicle prepped in paint booth; primer and base coat completed. Curing in progress.",
                    null,
                    now.minusDays(3), null,
                    new BigDecimal("37000"), null,
                    List.of(paint, ledHead));
        }

        // Order 5: Approved (Rohit - Honda City)
        if (android != null && speakers != null) {
            createRequest(customer1, v2, RequestStatus.APPROVED,
                    "Wireless Apple CarPlay & Android Auto integration with steering wheel audio controls.",
                    "Components received in inventory. Customer scheduled for installation this Saturday.",
                    null,
                    now.minusDays(5), null,
                    new BigDecimal("33000"), null,
                    List.of(android, speakers));
        }

        // Order 6: Pending (Arjun - Mahindra Thar)
        if (bodyKit != null && underglow != null) {
            createRequest(customer4, v5, RequestStatus.PENDING,
                    "Matte finish on body kit components and concealed wiring for underglow kit.",
                    null,
                    null,
                    now.minusDays(1), null,
                    new BigDecimal("42000"), null,
                    List.of(bodyKit, underglow));
        }

        // Order 7: Pending (Rahul - Hyundai Creta)
        if (camera != null) {
            createRequest(customer2, v3, RequestStatus.PENDING,
                    "OEM bumper camera mount installation with dynamic reverse trajectory lines.",
                    null,
                    null,
                    now, null,
                    new BigDecimal("4000"), null,
                    List.of(camera));
        }

        // Order 8: Rejected (Priya - VW Polo GT)
        if (turboTimer != null) {
            createRequest(customer3, v4, RequestStatus.REJECTED,
                    "Install standalone turbo timer kit for turbocharger cooling.",
                    null,
                    "Factory ECU immobilizer on this model does not support aftermarket standalone timer harness.",
                    now.minusDays(20), null,
                    new BigDecimal("5000"), null,
                    List.of(turboTimer));
        }

        logger.info("Successfully seeded customers, vehicles, orders, and feedback records!");
    }

    private User createCustomer(String name, String email, String phone) {
        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("Customer@1234"));
        user.setPhone(phone);
        user.setRole(User.Role.CUSTOMER);
        user.setStatus(User.AccountStatus.ACTIVE);
        return userRepository.save(user);
    }

    private Vehicle createVehicle(User owner, String regNo, String brand, String model, int year, String fuel, String color, String transmission) {
        Vehicle v = new Vehicle();
        v.setOwner(owner);
        v.setRegistrationNumber(regNo);
        v.setBrand(brand);
        v.setModel(model);
        v.setYear(year);
        v.setFuelType(fuel);
        v.setColor(color);
        v.setTransmission(transmission);
        return vehicleRepository.save(v);
    }

    private ModificationRequest createRequest(User customer, Vehicle vehicle, RequestStatus status,
                                              String customerNotes, String adminNotes, String rejectionReason,
                                              LocalDateTime reqDate, LocalDateTime completedAt,
                                              BigDecimal estimated, BigDecimal finalCost,
                                              List<ModificationService> services) {
        ModificationRequest req = new ModificationRequest();
        req.setCustomer(customer);
        req.setVehicle(vehicle);
        req.setStatus(status);
        req.setCustomerNotes(customerNotes);
        req.setAdminNotes(adminNotes);
        req.setRejectionReason(rejectionReason);
        req.setRequestDate(reqDate);
        req.setCompletedAt(completedAt);
        req.setEstimatedCost(estimated);
        req.setFinalCost(finalCost);
        req = requestRepository.save(req);

        for (ModificationService s : services) {
            RequestItem item = new RequestItem();
            item.setModificationRequest(req);
            item.setService(s);
            item.setQuantity(1);
            item.setUnitPrice(s.getBasePrice());
            item.setSubtotal(s.getBasePrice());
            requestItemRepository.save(item);
        }
        return req;
    }

    private void createFeedback(User customer, ModificationRequest request, int rating, String comments) {
        Feedback fb = new Feedback();
        fb.setCustomer(customer);
        fb.setRequest(request);
        fb.setRating(rating);
        fb.setComments(comments);
        feedbackRepository.save(fb);
    }

    private record ServiceData(String name, String description, BigDecimal price, String duration) {}
}
