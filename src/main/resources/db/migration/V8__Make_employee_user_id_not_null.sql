-- Drop foreign key constraint to modify user_id column
ALTER TABLE employees
DROP FOREIGN KEY fk_employee_user;

-- Delete employees without users (data cleanup)
DELETE FROM employees WHERE user_id IS NULL;

-- Make user_id column not null in employees table
ALTER TABLE employees
MODIFY COLUMN user_id INT NOT NULL;

-- Recreate foreign key constraint with ON DELETE CASCADE
ALTER TABLE employees
ADD CONSTRAINT fk_employee_user
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
