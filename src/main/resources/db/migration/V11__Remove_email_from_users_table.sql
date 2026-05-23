-- Remove email column from users table since email is now only stored in employees table
-- Drop the unique index on email first
DROP INDEX idx_user_email ON users;

-- Remove the email column
ALTER TABLE users DROP COLUMN email;
