-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS EMS_Project CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE EMS_Project;

-- Create users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_username (username),
    INDEX idx_user_enabled (enabled)
);

-- Create employees table
CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    hire_date DATE NOT NULL,
    job_title VARCHAR(100),
    salary DECIMAL(10,2),
    department VARCHAR(100),
    user_id INT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_employee_email (email),
    INDEX idx_employee_department (department),
    INDEX idx_employee_hire_date (hire_date),
    INDEX idx_employee_user_id (user_id)
);

-- Create authorities table
CREATE TABLE authorities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    authority VARCHAR(100) NOT NULL UNIQUE,
    INDEX idx_authority (authority)
);

-- Create roles table
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(50) NOT NULL UNIQUE,
    INDEX idx_role (role)
);

-- Create role_authorities join table
CREATE TABLE role_authorities (
    role_id INT NOT NULL,
    authority_id INT NOT NULL,
    PRIMARY KEY (role_id, authority_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (authority_id) REFERENCES authorities(id) ON DELETE CASCADE
);
-- Create user_roles join table
CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Add foreign key constraint for employees.user_id
ALTER TABLE employees 
ADD CONSTRAINT fk_employee_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;
