-- Hotel Management System Database Schema
-- Run this file in MySQL Workbench or the mysql CLI before running the app.

CREATE DATABASE IF NOT EXISTS hotel_management_system;
USE hotel_management_system;

-- Login / staff accounts
CREATE TABLE users (
    user_id      INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    full_name    VARCHAR(100) NOT NULL,
    role         VARCHAR(20) NOT NULL DEFAULT 'RECEPTIONIST', -- ADMIN / RECEPTIONIST
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rooms available in the hotel
CREATE TABLE rooms (
    room_id      INT AUTO_INCREMENT PRIMARY KEY,
    room_number  VARCHAR(10) NOT NULL UNIQUE,
    room_type    VARCHAR(30) NOT NULL,       -- SINGLE / DOUBLE / SUITE / DELUXE
    price_per_night DECIMAL(10,2) NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' -- AVAILABLE / OCCUPIED / MAINTENANCE
);

-- Customers / guests
CREATE TABLE customers (
    customer_id   INT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(50) NOT NULL,
    last_name     VARCHAR(50) NOT NULL,
    nic_passport  VARCHAR(30) NOT NULL UNIQUE,
    phone         VARCHAR(20) NOT NULL,
    email         VARCHAR(100),
    address       VARCHAR(255),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bookings (the main transaction table - joins customers + rooms + users)
CREATE TABLE bookings (
    booking_id     INT AUTO_INCREMENT PRIMARY KEY,
    customer_id    INT NOT NULL,
    room_id        INT NOT NULL,
    user_id        INT NOT NULL,             -- staff member who created the booking
    check_in_date  DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_amount   DECIMAL(10,2) NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED', -- CONFIRMED / CHECKED_IN / CHECKED_OUT / CANCELLED
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Sample data so you can log in immediately
-- password is "admin123" (plain text for coursework simplicity - see README for hashing note)
INSERT INTO users (username, password, full_name, role) VALUES
('admin', 'admin123', 'System Administrator', 'ADMIN'),
('reception', 'reception123', 'Front Desk Staff', 'RECEPTIONIST');

INSERT INTO rooms (room_number, room_type, price_per_night, status) VALUES
('101', 'SINGLE', 5000.00, 'AVAILABLE'),
('102', 'SINGLE', 5000.00, 'AVAILABLE'),
('201', 'DOUBLE', 8500.00, 'AVAILABLE'),
('202', 'DOUBLE', 8500.00, 'AVAILABLE'),
('301', 'SUITE', 15000.00, 'AVAILABLE'),
('302', 'DELUXE', 20000.00, 'AVAILABLE');
