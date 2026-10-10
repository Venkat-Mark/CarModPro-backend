-- ============================================================
-- Cars Modification Management System (CarModPro)
-- Complete MySQL Database DDL & Schema Script
-- ============================================================

CREATE DATABASE IF NOT EXISTS `car_modification_db`
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `car_modification_db`;

-- Drop tables in reverse order of foreign key dependency if re-running
DROP TABLE IF EXISTS `feedbacks`;
DROP TABLE IF EXISTS `request_items`;
DROP TABLE IF EXISTS `modification_requests`;
DROP TABLE IF EXISTS `vehicles`;
DROP TABLE IF EXISTS `modification_services`;
DROP TABLE IF EXISTS `modification_categories`;
DROP TABLE IF EXISTS `users`;

-- 1. Users table (Admin & Customer roles)
CREATE TABLE `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `phone` VARCHAR(15),
    `role` ENUM('ADMIN', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    `status` ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_email` (`email`),
    INDEX `idx_users_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Vehicles table
CREATE TABLE `vehicles` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `owner_id` BIGINT NOT NULL,
    `registration_number` VARCHAR(20) NOT NULL UNIQUE,
    `brand` VARCHAR(50) NOT NULL,
    `model` VARCHAR(50) NOT NULL,
    `year` INT NOT NULL,
    `fuel_type` VARCHAR(30) NOT NULL,
    `color` VARCHAR(30),
    `transmission` VARCHAR(50),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_vehicles_owner` (`owner_id`),
    INDEX `idx_vehicles_reg` (`registration_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Modification Categories table
CREATE TABLE `modification_categories` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    `description` VARCHAR(500),
    `icon_class` VARCHAR(100),
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Modification Services table
CREATE TABLE `modification_services` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `category_id` BIGINT NOT NULL,
    `name` VARCHAR(150) NOT NULL,
    `description` VARCHAR(2000),
    `base_price` DECIMAL(12, 2) NOT NULL,
    `duration` VARCHAR(50),
    `image_path` VARCHAR(255),
    `available` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`category_id`) REFERENCES `modification_categories` (`id`) ON DELETE RESTRICT,
    INDEX `idx_services_category` (`category_id`),
    INDEX `idx_services_available` (`available`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Modification Requests table
CREATE TABLE `modification_requests` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `vehicle_id` BIGINT NOT NULL,
    `status` ENUM('PENDING', 'APPROVED', 'IN_PROGRESS', 'COMPLETED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    `estimated_cost` DECIMAL(12, 2),
    `final_cost` DECIMAL(12, 2),
    `customer_notes` VARCHAR(1000),
    `admin_notes` VARCHAR(1000),
    `rejection_reason` VARCHAR(100),
    `request_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `completed_at` DATETIME,
    FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`) ON DELETE CASCADE,
    INDEX `idx_requests_customer` (`customer_id`),
    INDEX `idx_requests_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Request Items table (line items for each service requested)
CREATE TABLE `request_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `request_id` BIGINT NOT NULL,
    `service_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL DEFAULT 1,
    `unit_price` DECIMAL(12, 2) NOT NULL,
    `subtotal` DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (`request_id`) REFERENCES `modification_requests` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`service_id`) REFERENCES `modification_services` (`id`) ON DELETE RESTRICT,
    INDEX `idx_items_request` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Feedbacks table
CREATE TABLE `feedbacks` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `request_id` BIGINT NOT NULL UNIQUE,
    `rating` INT NOT NULL CHECK (`rating` >= 1 AND `rating` <= 5),
    `comments` VARCHAR(1000),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`request_id`) REFERENCES `modification_requests` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
