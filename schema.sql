-- HemoLink Blood Donor Matching System Database Schema
CREATE DATABASE IF NOT EXISTS blood_donation_db;
USE blood_donation_db;

-- Drop tables if exists (in reverse dependency order)
DROP TABLE IF EXISTS contact_messages;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS blood_requests;
DROP TABLE IF EXISTS donations;
DROP TABLE IF EXISTS blood_units;
DROP TABLE IF EXISTS hospitals;
DROP TABLE IF EXISTS donors;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS settings;

-- 1. users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- admin, donor, hospital
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. donors table
CREATE TABLE donors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    name VARCHAR(100) NOT NULL,
    blood_type VARCHAR(5) NOT NULL, -- A, B, AB, O
    rh_factor VARCHAR(2) NOT NULL,   -- +, -
    age INT NOT NULL,
    gender VARCHAR(20),
    city VARCHAR(100),
    address VARCHAR(255),
    weight INT NOT NULL,
    phone VARCHAR(20),
    medical_conditions TEXT,
    last_donation_date DATE,
    is_available BOOLEAN DEFAULT TRUE,
    total_donations INT DEFAULT 0,
    approval_status VARCHAR(20) DEFAULT 'awaiting', -- approved, awaiting, rejected
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. hospitals table
CREATE TABLE hospitals (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    name VARCHAR(150) NOT NULL,
    city VARCHAR(100),
    address VARCHAR(255),
    license VARCHAR(100),
    type VARCHAR(20) DEFAULT 'Private', -- Government, Private, Non-Profit
    approval_status VARCHAR(20) DEFAULT 'awaiting', -- approved, awaiting, rejected
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. blood_units table
CREATE TABLE blood_units (
    id INT PRIMARY KEY AUTO_INCREMENT,
    blood_type VARCHAR(5) NOT NULL,
    rh_factor VARCHAR(2) NOT NULL,
    volume_ml INT DEFAULT 450,
    collected_date DATE NOT NULL,
    expires_date DATE NOT NULL,
    donor_id INT NULL,
    donor_name VARCHAR(100),
    status VARCHAR(20) DEFAULT 'available', -- available, reserved, used, expired
    FOREIGN KEY (donor_id) REFERENCES donors(id) ON DELETE SET NULL
);

-- 5. donations table
CREATE TABLE donations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT NULL,
    donor_id INT NOT NULL,
    donor_name VARCHAR(100) NOT NULL,
    blood_type VARCHAR(5) NOT NULL,
    rh_factor VARCHAR(2) NOT NULL,
    volume_ml INT DEFAULT 450,
    request_date DATETIME NULL,
    donated_at DATETIME NOT NULL,
    hospital_name VARCHAR(150),
    hospital_id INT,
    status VARCHAR(20) DEFAULT 'completed',
    hospital_note TEXT,
    reference_id VARCHAR(50),
    notes TEXT,
    FOREIGN KEY (donor_id) REFERENCES donors(id) ON DELETE CASCADE,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE SET NULL
);

-- 6. blood_requests table
CREATE TABLE blood_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_name VARCHAR(100) NOT NULL,
    blood_type VARCHAR(5) NOT NULL,
    rh_factor VARCHAR(2) NOT NULL,
    units_needed INT NOT NULL,
    urgency VARCHAR(20) DEFAULT 'routine', -- critical, urgent, routine
    hospital_id INT NOT NULL,
    hospital_name VARCHAR(150) NOT NULL,
    city VARCHAR(100),
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    notes TEXT,
    required_date DATE NULL,
    status VARCHAR(20) DEFAULT 'pending', -- pending, matching, accepted, fulfilled, completed, cancelled
    matched_donor_ids TEXT,
    accepted_donor_ids TEXT,
    assigned_donor_id INT NULL,
    accepted_units INT DEFAULT 0,
    accepted_at DATETIME NULL,
    hospital_note TEXT,
    reference_id VARCHAR(50),
    donor_responses TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_donor_id) REFERENCES donors(id) ON DELETE SET NULL
);

-- 7. notifications table
CREATE TABLE notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(20) DEFAULT 'info', -- request, success, match, alert, approval, info
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 8. contact_messages table
CREATE TABLE contact_messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'unread', -- unread, read
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 9. settings table
CREATE TABLE settings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(50) UNIQUE NOT NULL,
    setting_value VARCHAR(255) NOT NULL
);

-- Default Settings Seed Data
INSERT INTO settings (setting_key, setting_value) VALUES
('system_name', 'HemoLink'),
('cooldown_days', '56'),
('min_weight', '50'),
('min_age', '18'),
('max_age', '65'),
('unit_volume', '450'),
('expiry_days', '42'),
('low_stock_threshold', '3'),
('emergency_sms', 'true'),
('auto_match', 'false');

-- SHA-256 Hashed Passwords:
-- admin -> 8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918 (for 'admin')
-- donor -> 0471b058a5f36e4f323a677efd7e6c986c757c91350a4d46816007e997f6c4be (for 'donor')
-- hospital -> 5f7c320d7f96593a200f68c347f722a46c243f60f64c67425102f9e4eb5ec746 (for 'hospital')

-- Seed Users
INSERT INTO users (username, password, role, name, email, phone) VALUES
('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'admin', 'System Admin', 'admin@hemolink.lk', '0770000001'),
('donor1', '0471b058a5f36e4f323a677efd7e6c986c757c91350a4d46816007e997f6c4be', 'donor', 'Kasun Perera', 'kasun@mail.lk', '0771234567'),
('donor2', '0471b058a5f36e4f323a677efd7e6c986c757c91350a4d46816007e997f6c4be', 'donor', 'Chamari Fernando', 'chamari@mail.lk', '0712345678'),
('donor3', '0471b058a5f36e4f323a677efd7e6c986c757c91350a4d46816007e997f6c4be', 'donor', 'Nuwan Silva', 'nuwan@mail.lk', '0763456789'),
('donor4', '0471b058a5f36e4f323a677efd7e6c986c757c91350a4d46816007e997f6c4be', 'donor', 'Dilani Jayasinghe', 'dilani@mail.lk', '0754567890'),
('donor5', '0471b058a5f36e4f323a677efd7e6c986c757c91350a4d46816007e997f6c4be', 'donor', 'Kusal Mendis', 'kusal@mail.lk', '0705678901'),
('donor6', '0471b058a5f36e4f323a677efd7e6c986c757c91350a4d46816007e997f6c4be', 'donor', 'Tharushi Ranasinghe', 'tharushi@mail.lk', '0726789012'),
('donor_await', '0471b058a5f36e4f323a677efd7e6c986c757c91350a4d46816007e997f6c4be', 'donor', 'Ruwan Bandara', 'ruwan@mail.lk', '0787890123'),
('hosp1', '5f7c320d7f96593a200f68c347f722a46c243f60f64c67425102f9e4eb5ec746', 'hospital', 'National Hospital Colombo', 'nhsl@hosp.lk', '0112691111'),
('hosp2', '5f7c320d7f96593a200f68c347f722a46c243f60f64c67425102f9e4eb5ec746', 'hospital', 'Teaching Hospital Kandy', 'kandy@hosp.lk', '0812222261'),
('hosp_await', '5f7c320d7f96593a200f68c347f722a46c243f60f64c67425102f9e4eb5ec746', 'hospital', 'Lanka Hospitals Colombo', 'lanka@hosp.lk', '0115430000');

-- Seed Donors
INSERT INTO donors (user_id, name, blood_type, rh_factor, age, gender, city, address, weight, phone, medical_conditions, last_donation_date, is_available, total_donations, approval_status) VALUES
(2, 'Kasun Perera', 'O', '-', 28, 'Male', 'Colombo', 'Cinnamon Gardens', 72, '0771234567', 'None', NULL, TRUE, 0, 'approved'),
(3, 'Chamari Fernando', 'A', '+', 32, 'Female', 'Kandy', 'Peradeniya', 58, '0712345678', 'None', '2024-09-15', TRUE, 3, 'approved'),
(4, 'Nuwan Silva', 'B', '+', 25, 'Male', 'Galle', 'Fort Main St', 68, '0763456789', 'None', NULL, TRUE, 1, 'approved'),
(5, 'Dilani Jayasinghe', 'AB', '+', 30, 'Female', 'Colombo', 'Wellawatte', 62, '0754567890', 'None', '2024-05-10', TRUE, 2, 'approved'),
(6, 'Kusal Mendis', 'O', '+', 35, 'Male', 'Gampaha', 'Negombo Rd', 78, '0705678901', 'None', NULL, TRUE, 4, 'approved'),
(7, 'Tharushi Ranasinghe', 'A', '-', 27, 'Female', 'Kurunegala', 'Bauddhaloka Mawatha', 54, '0726789012', 'None', NULL, TRUE, 1, 'approved'),
(8, 'Ruwan Bandara', 'AB', '-', 40, 'Male', 'Jaffna', 'Nallur', 75, '0787890123', 'None', NULL, TRUE, 0, 'awaiting');

-- Seed Hospitals
INSERT INTO hospitals (user_id, name, city, address, license, type, approval_status) VALUES
(9, 'National Hospital Colombo', 'Colombo', 'E. W. Perera Mawatha', 'SL-2024-001', 'Government', 'approved'),
(10, 'Teaching Hospital Kandy', 'Kandy', 'William Gopallawa Mawatha', 'SL-2024-015', 'Government', 'approved'),
(11, 'Lanka Hospitals Colombo', 'Colombo', '578 Elvitigala Mawatha', 'SL-2024-088', 'Private', 'awaiting');

-- Seed Blood Units
INSERT INTO blood_units (blood_type, rh_factor, volume_ml, collected_date, expires_date, donor_id, donor_name, status) VALUES
('O', '-', 450, '2025-01-05', '2025-02-16', 1, 'Kasun Perera', 'available'),
('A', '+', 450, '2025-01-08', '2025-02-19', NULL, 'Walk-in', 'available'),
('B', '+', 450, '2025-01-06', '2025-02-17', 3, 'Nuwan Silva', 'available'),
('O', '+', 450, '2025-01-11', '2025-02-22', NULL, 'Walk-in', 'available'),
('A', '-', 450, '2025-01-04', '2025-02-15', NULL, 'Walk-in', 'available');

-- Seed Donations
INSERT INTO donations (request_id, donor_id, donor_name, blood_type, rh_factor, volume_ml, request_date, donated_at, hospital_name, hospital_id, status, hospital_note, reference_id, notes) VALUES
(1, 2, 'Chamari Fernando', 'A', '+', 450, '2024-09-14 09:00:00', '2024-09-15 10:30:00', 'National Hospital Colombo', 1, 'completed', 'Donation successfully completed. Donor arrived on time.', 'DON-2024-001', 'Routine voluntary donation'),
(2, 3, 'Nuwan Silva', 'B', '+', 450, '2025-01-05 10:00:00', '2025-01-06 14:00:00', 'Teaching Hospital Kandy', 2, 'completed', 'Blood units received successfully.', 'DON-2025-002', 'Voluntary blood drive'),
(3, 5, 'Kusal Mendis', 'O', '+', 450, '2024-11-19 15:00:00', '2024-11-20 11:15:00', 'National Hospital Colombo', 1, 'completed', 'Emergency response donation.', 'DON-2024-003', 'Emergency response donation'),
(4, 4, 'Dilani Jayasinghe', 'AB', '+', 450, '2024-05-09 08:30:00', '2024-05-10 09:45:00', 'National Hospital Colombo', 1, 'completed', 'Voluntary donation received.', 'DON-2024-004', 'Voluntary donation');

-- Seed Blood Requests
INSERT INTO blood_requests (patient_name, blood_type, rh_factor, units_needed, urgency, hospital_id, hospital_name, city, contact_person, phone, notes, status, reference_id) VALUES
('Sahan Wickramasinghe', 'O', '+', 2, 'critical', 1, 'National Hospital Colombo', 'Colombo', 'Dr. A. Wickramasinghe', '0112691111', 'Post-surgery transfusion', 'pending', 'REQ-2026-001'),
('Malini Perera', 'A', '-', 1, 'urgent', 2, 'Teaching Hospital Kandy', 'Kandy', 'Dr. K. Jayawardena', '0812222261', 'Anemia during pregnancy', 'pending', 'REQ-2026-002'),
('Dinesh Gunawardena', 'B', '+', 3, 'routine', 1, 'National Hospital Colombo', 'Colombo', 'Dr. N. Rajapaksha', '0112691111', 'Scheduled orthopedic surgery', 'pending', 'REQ-2026-003');

-- Seed Notifications
INSERT INTO notifications (user_id, title, message, type, is_read, created_at) VALUES
(1, 'New Donor Registration', 'Ruwan Bandara registered as a donor and requires approval.', 'approval', FALSE, NOW()),
(1, 'New Hospital Registration', 'Lanka Hospitals Colombo registered as a hospital and requires approval.', 'approval', FALSE, NOW()),
(2, 'Welcome to HemoLink', 'Your donor profile is active. Thank you for registering as a blood donor.', 'info', TRUE, NOW()),
(9, 'Blood Request Created', 'Request #1 for O+ blood created successfully.', 'success', TRUE, NOW());

