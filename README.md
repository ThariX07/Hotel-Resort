#  Ocean View Resort - Front Desk Management System

A robust, enterprise-level B2B Point-of-Sale (POS) and reservation management system built for hotel staff. This application handles real-time room booking, secure session management, and automated ledger updates without requiring page reloads.

##  Key Features

* **Unified Staff POS Dashboard:** A split-screen interface allowing staff to process new walk-in bookings on one side while monitoring a live, asynchronously updating ledger on the other.
* **RESTful Asynchronous Communication:** Utilizes the JavaScript `fetch` API to communicate with Java Servlets, creating a seamless Single Page Application (SPA) experience.
* **Secure Session Management:** Implements Jakarta EE `HttpSession` to validate staff authorization and protect endpoints from unauthorized direct URL access.
* **Modern Java Architecture:** Built using Data Transfer Objects (DTO) for efficient data packaging, the Singleton pattern for database connections, and custom Gson adapters to handle Java 23 `LocalDate` security constraints.
* **Database Integrity:** Utilizes SQL joins, foreign key constraints, and multi-table transaction rollbacks to ensure data consistency between users, guests, rooms, and reservations.

##  Technology Stack

* **Backend:** Java 23 (JDK 23), Jakarta EE (Servlets)
* **Build Tool:** Maven
* **Server:** Apache Tomcat 10.1+
* **Database:** MySQL 8.0+ / JDBC
* **Frontend:** HTML5, CSS3, Bootstrap 5.3, Vanilla JavaScript
* **Data Parsing:** Google Gson
* **CI/CD:** GitHub Actions

##  Installation & Setup Guide

Follow these steps to run the application locally:

### 1. Prerequisites
Ensure you have the following installed:
* Java Development Kit (JDK) 23
* Apache Tomcat 10.1+
* MySQL Server & phpMyAdmin (e.g., via XAMPP)
* IntelliJ IDEA (Enterprise or Community with Smart Tomcat plugin)

### 2. Database Configuration
1. Open phpMyAdmin and create a new database named `oceanview_db`.
2. Execute the following SQL script to generate the schema and seed the initial data:

```sql
-- Disable foreign key checks for clean rebuild
SET FOREIGN_KEY_CHECKS = 0;

-- Create Users (Staff) Table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'STAFF'
);

-- Insert Default Admin/Staff Account
INSERT INTO users (username, password, role) VALUES ('admin', 'password123', 'ADMIN');

-- Create Guests Table
CREATE TABLE guests (
    guest_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    contact_number VARCHAR(20)
);

-- Create Rooms Table
CREATE TABLE rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    price_per_night DOUBLE NOT NULL,
    is_available BOOLEAN DEFAULT TRUE
);

-- Seed Room Data
INSERT INTO rooms (room_id, room_number, room_type, price_per_night, is_available) VALUES 
(1, '101', 'Ocean View Suite', 15000.00, TRUE),
(2, '102', 'Standard Deluxe', 10000.00, TRUE),
(3, '103', 'Family Room', 20000.00, TRUE);

-- Create Reservations Table
CREATE TABLE reservations (
    reservation_number VARCHAR(20) PRIMARY KEY,
    guest_id INT,
    room_id INT,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_cost DOUBLE NOT NULL,
    user_id INT,
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

SET FOREIGN_KEY_CHECKS = 1;
