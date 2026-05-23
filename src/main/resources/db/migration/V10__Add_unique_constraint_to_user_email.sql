-- Add unique constraint to users.email column
-- First, delete duplicate emails (keep the first occurrence)
DELETE u1 FROM users u1
INNER JOIN users u2
WHERE u1.id > u2.id
AND u1.email = u2.email
AND u1.email IS NOT NULL;

-- Add unique index on email column
CREATE UNIQUE INDEX idx_user_email ON users(email);
