-- Remove verified column from email_verification_tokens table
-- Tokens are now deleted after successful verification instead of being marked as verified
ALTER TABLE email_verification_tokens DROP COLUMN verified;
