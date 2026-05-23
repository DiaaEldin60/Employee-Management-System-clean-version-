-- Add email and is_temporary_password fields for account completion
ALTER TABLE users
ADD COLUMN is_temporary_password BOOLEAN NOT NULL DEFAULT TRUE AFTER enabled;

ALTER TABLE users
ADD COLUMN email VARCHAR(150) AFTER is_temporary_password;

