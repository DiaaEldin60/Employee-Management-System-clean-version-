-- Drop department column from users table (redundant - department is in employees table)
ALTER TABLE users
DROP COLUMN department;
