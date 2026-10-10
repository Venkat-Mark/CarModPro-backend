# ⚙️ Backend Architecture - CarModPro

The backend is built with **Java 17 / 21**, **Spring Boot 3.2.0**, **Spring Security**, **Spring Data JPA**, and **Hibernate**.

---

## 📂 Package Architecture

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/carmod/app/
│   │   │   ├── CarModificationManagementApplication.java   # Spring Boot Application Entry
│   │   │   │
│   │   │   ├── config/                                     # System Configuration
│   │   │   │   ├── SecurityConfig.java                     # Spring Security, RBAC & CSRF
│   │   │   │   └── DataInitializer.java                    # Automated DB seeder & Admin setup
│   │   │   │
│   │   │   ├── controller/                                 # Spring MVC Request Routing
│   │   │   │   ├── HomeController.java                     # Public home, catalog & service detail
│   │   │   │   ├── AuthController.java                     # Login & registration processing
│   │   │   │   ├── CustomerController.java                 # Garage, orders, profile & estimate API
│   │   │   │   ├── AdminController.java                    # KPIs, customer & order management
│   │   │   │   └── CustomErrorController.java              # 403, 404, and 500 error routes
│   │   │   │
│   │   │   ├── dto/                                        # Data Transfer Objects
│   │   │   │   ├── UserRegistrationDto.java                # Registration validation
│   │   │   │   ├── UserProfileDto.java                     # Profile update & password change
│   │   │   │   ├── ModificationRequestDto.java             # Multi-service booking payload
│   │   │   │   ├── RequestStatusUpdateDto.java             # Admin status & pricing update
│   │   │   │   └── AdminDashboardDto.java                  # KPIs & Chart.js analytical data
│   │   │   │
│   │   │   ├── entity/                                     # JPA / Hibernate Domain Entities
│   │   │   │   ├── User.java                               # Customer & Admin user accounts
│   │   │   │   ├── Vehicle.java                            # Registered customer cars
│   │   │   │   ├── ModificationCategory.java               # Catalog categories
│   │   │   │   ├── ModificationService.java                # Modification service items
│   │   │   │   ├── ModificationRequest.java                # Order lifecycle & state machine
│   │   │   │   ├── RequestItem.java                        # Order line items with subtotals
│   │   │   │   └── Feedback.java                           # Post-service 5-star ratings & reviews
│   │   │   │
│   │   │   ├── repository/                                 # Spring Data JPA Repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── VehicleRepository.java
│   │   │   │   ├── ModificationCategoryRepository.java
│   │   │   │   ├── ModificationServiceRepository.java
│   │   │   │   ├── ModificationRequestRepository.java
│   │   │   │   ├── RequestItemRepository.java
│   │   │   │   └── FeedbackRepository.java
│   │   │   │
│   │   │   └── service/                                    # Transactional Business Logic
│   │   │       ├── CustomUserDetailsService.java           # User details loader for Security
│   │   │       ├── UserService.java                        # Account registration & management
│   │   │       ├── VehicleService.java                     # Garage CRUD & ownership verification
│   │   │       ├── ModificationServiceManagement.java      # Catalog CRUD & availability toggling
│   │   │       ├── ModificationRequestService.java         # Order pricing & status transitions
│   │   │       └── FeedbackService.java                    # Customer reviews & moderation
│   │   │
│   │   └── resources/
│   │       └── application.properties                      # Database, JPA, and server settings
│   │
│   └── test/java/com/carmod/app/                           # Automated Test Suites
│       ├── controller/
│       │   └── SecurityAccessTest.java                     # Route authorization tests
│       └── service/
│           ├── UserServiceTest.java                        # User service unit tests
│           └── ModificationRequestServiceTest.java         # Order & calculation unit tests
```

---

## 🔒 Security Architecture
- **Password Encoding**: `BCryptPasswordEncoder` with strength 12.
- **Role-Based Access Control**:
  - `ROLE_ADMIN`: Full access to `/admin/**`.
  - `ROLE_CUSTOMER`: Restricted access to personal `/customer/**` routes.
  - Anonymous: Access to `/`, `/services/**`, `/login`, `/register`.
- **CSRF Protection**: Enabled on all form submissions.
