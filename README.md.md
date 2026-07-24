# HemoLink — Blood Donor Matching System

HemoLink is a full-stack university final-year web application for managing blood donors, hospital requests, inventory stock, and automated donor-to-request matching based on compatibility, location, and donation cooldown rules.

## Technology Stack

- **Backend**: Java, Java Servlets, JDBC, Apache Tomcat 9/10
- **Frontend**: HTML5, Vanilla CSS / Tailwind-inspired Dark Theme, Vanilla JavaScript, Chart.js (Dashboard Analytics)
- **Database**: MySQL 5.7 / 8.0

---

## Database Setup

1. Open your MySQL client (MySQL Command Line, phpMyAdmin, or MySQL Workbench).
2. Execute the `schema.sql` file located in the root directory:
   ```sql
   SOURCE c:/xampp/htdocs/bloodDonationSystem/schema.sql;
   ```
3. Update `src/main/java/com/blooddonation/util/DBConnection.java` with your local MySQL password if needed:
   ```java
   private static final String PASSWORD = "your_mysql_password";
   ```

---

## Deployment & Running

1. Open the project in your Java IDE (e.g. IntelliJ IDEA or Eclipse).
2. Compile and package the WAR file or deploy the project directly to Apache Tomcat server.
3. Open a browser and navigate to:
   ```
   http://localhost:8080/BloodDonationSystem/
   ```

---

## Default Login Credentials

- **Admin Portal**: `admin` / `admin`
- **Donor Portal**: `donor1` / `donor`
- **Hospital Portal**: `hosp1` / `hospital`

---

## Key System Features

1. **Role-Based Portals**: Admin Dashboard, Donor Portal, Hospital Portal.
2. **Automated Donor Matching Algorithm**: Simple, readable scoring algorithm (0–100) combining blood group compatibility matrix, city location match, availability status, and 56-day donation cooldown eligibility.
3. **Blood Stock Inventory**: Real-time stock summary with **LOW STOCK** alert warnings when units drop below configurable threshold.
4. **Approval Workflow**: Admin approval system for new donor and hospital registrations.
5. **Chart Analytics**: Visual breakdown of inventory units and donor group distributions using Chart.js.
