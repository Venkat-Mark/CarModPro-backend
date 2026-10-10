# 🏎️ Cars Modification Management System (CarModPro)

A complete, production-grade automotive customization and modification tracking platform built with **Java 17 / 21**, **Spring Boot 3**, **Spring Security**, **Thymeleaf**, **Bootstrap 5**, **Chart.js**, and **MySQL / Embedded Database**.

The project is structured into **`frontend/`** and **`backend/`** modules for clear separation of concerns, modern architectural modularity, and easy presentation.

---

## 📁 Project Architecture & Folder Structure

```
c:\TT car project
├── frontend/                                   # 🎨 FRONTEND MODULE (UI, Templates, Styles & Scripts)
│   ├── templates/                              # Thymeleaf HTML5 Server-Rendered Views
│   │   ├── home.html                           # Showroom landing page
│   │   ├── services.html                       # Modification catalog with category filters
│   │   ├── service-detail.html                 # Service details & instant request booking CTA
│   │   ├── admin/                              # Administrator Control Panel
│   │   │   ├── dashboard.html                  # Revenue KPIs, orders chart, status donut
│   │   │   ├── requests.html                   # Modification order management & filters
│   │   │   ├── request-detail.html             # Order invoice, pricing & status updates
│   │   │   ├── services.html                   # Modification catalog CRUD
│   │   │   ├── service-form.html               # Add / Edit service form
│   │   │   ├── categories.html                 # Category manager with icons
│   │   │   ├── customers.html                  # Customer directory & account suspension
│   │   │   ├── customer-detail.html            # Customer profile, garage & order history
│   │   │   ├── vehicles.html                   # System-wide vehicle registry
│   │   │   └── feedback.html                   # Review moderation & ratings inspection
│   │   ├── customer/                           # Customer Self-Service Portal
│   │   │   ├── dashboard.html                  # Customer home with garage summary
│   │   │   ├── vehicles.html                   # Customer's vehicle garage
│   │   │   ├── vehicle-form.html               # Add / Edit vehicle form with validation
│   │   │   ├── request-new.html                # Service picker with dynamic price calculator
│   │   │   ├── requests.html                   # Modification order tracking & cancellation
│   │   │   ├── request-detail.html             # Order details & visual progress timeline
│   │   │   ├── profile.html                    # Profile settings & password change
│   │   │   └── feedback-form.html              # Interactive 5-star rating submission
│   │   ├── auth/                               # Authentication Pages
│   │   │   ├── login.html                      # Dual-role login with demo credentials
│   │   │   └── register.html                   # Registration with password strength meter
│   │   ├── error/                              # Custom Error Pages
│   │   │   ├── 403.html                        # Access Denied / Unauthorized
│   │   │   ├── 404.html                        # Page Not Found
│   │   │   └── 500.html                        # Server Error with reload option
│   │   └── fragments/                          # Reusable Modular UI Components
│   │       ├── navbar.html                     # Navigation header, alerts, footer
│   │       └── layout.html                     # Collapsible admin sidebar, topbar, customer nav
│   ├── static/                                 # Static Assets & Styling
│   │   ├── css/                                # CSS Stylesheets (Showroom theme)
│   │   │   ├── main.css                        # Dark showroom aesthetic, hero, cards, badges
│   │   │   ├── admin.css                       # Collapsible sidebar, metrics, topbar
│   │   │   └── customer.css                    # Garage cards, service selectors, star rating
│   │   ├── js/                                 # Client-side JavaScript
│   │   │   ├── main.js                         # Auto-dismiss alerts, tooltips, confirmations
│   │   │   └── admin.js                        # Sidebar toggle & persistence via localStorage
│   │   └── images/                             # Image assets
│   └── README.md                               # Detailed Frontend Documentation
│
├── backend/                                    # ⚙️ BACKEND MODULE (Spring Boot, Security & Persistence)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/carmod/app/
│   │   │   │   ├── CarModificationManagementApplication.java
│   │   │   │   ├── config/                     # SecurityFilterChain & DataInitializer
│   │   │   │   ├── controller/                 # Spring MVC Controllers (Home, Auth, Customer, Admin)
│   │   │   │   ├── dto/                        # Data Transfer Objects (Validation & Dashboard)
│   │   │   │   ├── entity/                     # JPA Entities (User, Vehicle, Service, Request, Feedback)
│   │   │   │   ├── repository/                 # Spring Data JPA Repositories
│   │   │   │   └── service/                    # Transactional Business Services
│   │   │   └── resources/
│   │   │       └── application.properties      # MySQL & H2 configuration, credentials, JPA
│   │   └── test/java/com/carmod/app/           # Automated Unit & Integration Tests (12/12 passing)
│   │       ├── controller/SecurityAccessTest.java
│   │       └── service/
│   │           ├── UserServiceTest.java
│   │           └── ModificationRequestServiceTest.java
│   └── README.md                               # Detailed Backend Documentation
│
├── schema.sql                                  # MySQL DDL Database Schema Script
└── pom.xml                                     # Root Maven Project Configuration
```

---

## 🚀 Running the Application

Both frontend and backend are orchestrated cleanly through Maven:

```powershell
cd "c:\TT car project"
mvn spring-boot:run
```

Access the application in your browser:
👉 **`http://localhost:8080`**

---

## 🔑 Login Credentials

### Administrator
- **Email / User ID**: **`admin@carmod.com`**
- **Password**: **`Admin@1234`**
- **Role**: `ADMIN`

### Pre-Seeded Customer
- **Email**: **`customer@carmod.com`**
- **Password**: **`Customer@1234`**
- **Name**: Rohit Sharma (Garage: BMW 330i, Honda City)
