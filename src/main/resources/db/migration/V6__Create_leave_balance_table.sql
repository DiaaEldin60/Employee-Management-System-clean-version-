-- Create leave_balance table
CREATE TABLE leave_balance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    annual_leave INT NOT NULL DEFAULT 21,
    emergency_leave INT NOT NULL DEFAULT 3,
    sick_leave INT NOT NULL DEFAULT 15,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    UNIQUE KEY idx_leave_balance_employee (employee_id)
);
