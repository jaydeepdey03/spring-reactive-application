-- V2__add_user_role.sql
ALTER TABLE users
ADD COLUMN role VARCHAR(50);
UPDATE users
SET role = 'USER'
WHERE role IS NULL;
ALTER TABLE users
ALTER COLUMN role
SET DEFAULT 'USER';
ALTER TABLE users
ALTER COLUMN role
SET NOT NULL;
ALTER TABLE users
ADD CONSTRAINT users_role_check CHECK (role IN ('USER', 'ADMIN'));
CREATE INDEX idx_users_role ON users(role);