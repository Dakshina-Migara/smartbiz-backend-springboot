# 🚀 SmartBiz Backend

SmartBiz Backend is a scalable **Spring Boot REST API** powering an AI-driven **ERP-lite** management platform designed for Small and Medium Enterprises (SMEs).

The system streamlines core business operations—sales, inventory, and financial tracking—while leveraging **OpenAI** to provide intelligent reporting and automated business communication.

---

## 📌 Project Highlights
*   **AI-Driven Insights**: Natural language querying for business data (e.g., "Show me my top sellers").
*   **Role-Based Access**: Dual-portal architecture for **System Admins** and **Business Owners**.
*   **ERP-Lite Features**: Complete management of inventory, customers, suppliers, and invoices.
*   **Scalable Architecture**: Clean, modular code following professional Spring Boot standards.
*   **Secure & Reliable**: Transactional data management with robust error handling.

---

## 🏗 Tech Stack

| Component | Technology |
| :--- | :--- |
| **Framework** | Spring Boot 3.x |
| **Language** | Java 17 |
| **Database** | MySQL |
| **ORM** | Spring Data JPA / Hibernate |
| **AI Integration** | OpenAI API |
| **Security** | Role-Based Authorization |
| **Build Tool** | Maven |
| **Documentation** | Swagger / OpenAPI (Planned) |

---

## 🧱 Module Overview

### 🔹 API Layer
Clean RESTful endpoints categorized by functionality:
*   `/api/v1/auth`: Registration and Intelligent Login.
*   `/api/v1/admin`: System-wide monitoring and subscription management.
*   `/api/v1/business`: Core ERP operations for business owners.
*   `/api/v1/ai`: Intelligence engine for automated reporting.

### 🔹 Core Domains
*   **Inventory**: Stock level monitoring, low-stock alerts, and product management.
*   **Sales & Invoicing**: Automated invoice generation and history tracking.
*   **Financials**: Transaction logging (Income/Expense) and profit analysis.
*   **Stakeholders**: Comprehensive Customer and Supplier directories.

---

## 🔐 Getting Started

### Prerequisites
*   Java 17 or higher
*   Maven 3.x
*   MySQL 8.x

### Installation
1.  **Clone the repository**:
    ```bash
    git clone https://github.com/[your-repo]/smartbiz-backend.git
    cd smartbiz-backend
    ```

2.  **Configure Database**:
    Update `src/main/resources/application.properties` with your MySQL credentials:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/smartbiz_db
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```

3.  **Run the Application**:
    ```bash
    mvn spring-boot:run
    ```

---

## 🧪 Testing with Postman

### Authentication Flow
1.  **Register**: `POST /api/v1/auth/register`
    *   Include `role: "ADMIN"` or `"OWNER"` in the request body.
2.  **Login**: `POST /api/v1/auth/login`
    *   The response provides the `accessibleArea` (e.g., `ADMIN_PORTAL`) and the `homePath` for navigation.

---

## 🔮 Future Roadmap
*   [ ] **JWT Implementation**: Fully stateless security with JWT tokens.
*   [ ] **Dockerization**: Containerized deployment for easy scaling.
*   [ ] **Payment Gateway**: Integration with Stripe/Razorpay for subscription billing.
*   [ ] **Real-time Notifications**: WebSocket integration for low-stock alerts.

---

## 👨‍💻 Project Governance
Developed by **Dakshina Migara** as part of the SmartBiz AI ERP ecosystem.  

---

## 📄 License
This project is for academic and portfolio purposes. All rights reserved.