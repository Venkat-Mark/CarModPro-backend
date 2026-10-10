# 🎨 Frontend Architecture - CarModPro

The frontend is built using **Server-Side Rendered (SSR) Thymeleaf 3**, **HTML5**, **CSS3**, **Bootstrap 5.3.0**, **Bootstrap Icons 1.11.3**, and **Chart.js 4.4.0**.

---

## 📂 Directory Layout

```
frontend/
├── templates/
│   ├── home.html                    # Public showroom landing page
│   ├── services.html                # Modification catalog with category pills & search
│   ├── service-detail.html          # Individual service detail with instant booking CTA
│   │
│   ├── admin/                       # Administrator Control Center
│   │   ├── dashboard.html           # Revenue KPIs, order breakdown, Chart.js graphs
│   │   ├── requests.html            # Modification orders management & filter
│   │   ├── request-detail.html      # Order detail, price finalization & status updates
│   │   ├── services.html            # Services catalog CRUD & availability toggle
│   │   ├── service-form.html        # Create / Edit service form
│   │   ├── categories.html          # Categories manager with icons & active toggle
│   │   ├── customers.html           # Customer account directory & status suspension
│   │   ├── customer-detail.html     # Customer profile with garage & past order history
│   │   ├── vehicles.html            # System-wide vehicle registry
│   │   └── feedback.html            # Review moderation & ratings inspection
│   │
│   ├── customer/                    # Customer Portal
│   │   ├── dashboard.html           # Customer home with garage summary & recent orders
│   │   ├── vehicles.html            # Customer's vehicle garage with search
│   │   ├── vehicle-form.html        # Add / Edit vehicle form with validation
│   │   ├── request-new.html         # Service selection with real-time price calculator
│   │   ├── requests.html            # Modification order tracking & cancellation
│   │   ├── request-detail.html      # Order items invoice & visual progress timeline
│   │   ├── profile.html             # Profile settings & password change
│   │   └── feedback-form.html       # Interactive 5-star rating submission
│   │
│   ├── auth/                        # Authentication
│   │   ├── login.html               # Dual-role sign in with demo credentials helper
│   │   └── register.html            # Registration with password strength meter
│   │
│   ├── error/                       # HTTP Error Pages
│   │   ├── 403.html                 # Access Denied / Unauthorized
│   │   ├── 404.html                 # Resource Not Found
│   │   └── 500.html                 # Internal Server Error with reload option
│   │
│   └── fragments/                   # Reusable Components
│       ├── navbar.html              # Public header, navigation links, alerts, footer
│       └── layout.html              # Collapsible admin sidebar, topbar, customer nav
│
└── static/
    ├── css/
    │   ├── main.css                 # Dark showroom theme (charcoal, navy, gold accents)
    │   ├── admin.css                # Collapsible sidebar, admin cards, topbar
    │   └── customer.css             # Garage cards, service selectors, star ratings
    ├── js/
    │   ├── main.js                  # Alert auto-dismiss, tooltip init, action confirmations
    │   └── admin.js                 # Sidebar toggle & state persistence via localStorage
    └── images/                      # Automotive graphics and icons
```

---

## 🎨 Design System & Theme
- **Theme**: Luxury automotive showroom aesthetic.
- **Background Palette**: Deep charcoal (`#0c0e14`) and rich navy (`#141722`).
- **Accents**: Premium gold (`#ffb703`, hover `#fb8500`) and electric cyan (`#00b4d8`).
- **Typography**: Clean sans-serif with strong visual hierarchy.
- **Components**: Rounded cards, subtle border highlights, status badges, and interactive feedback controls.
