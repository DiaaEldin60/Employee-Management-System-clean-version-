-- Insert default authorities
INSERT INTO authorities (authority) VALUES 
('CREATE_USER'),
('DELETE_USER'),
('UPDATE_USER'),
('VIEW_USER'),
('CREATE_EMPLOYEE'),
('DELETE_EMPLOYEE'),
('UPDATE_EMPLOYEE'),
('VIEW_EMPLOYEE'),
('MANAGE_ROLES'),
('VIEW_REPORTS');

-- Insert default roles
INSERT INTO roles (role) VALUES 
('ADMIN'),
('MANAGER'),
('EMPLOYEE');

-- Assign authorities to roles
-- Admin gets all authorities
INSERT INTO role_authorities (role_id, authority_id) 
SELECT r.id, a.id FROM roles r, authorities a WHERE r.role = 'ADMIN';

-- Manager gets limited authorities
INSERT INTO role_authorities (role_id, authority_id) 
SELECT r.id, a.id FROM roles r, authorities a 
WHERE r.role = 'MANAGER' AND a.authority IN (
    'VIEW_EMPLOYEE', 'UPDATE_EMPLOYEE', 'CREATE_EMPLOYEE', 'VIEW_USER', 'VIEW_REPORTS'
);

-- Employee gets basic authorities
INSERT INTO role_authorities (role_id, authority_id) 
SELECT r.id, a.id FROM roles r, authorities a 
WHERE r.role = 'EMPLOYEE' AND a.authority IN ('VIEW_EMPLOYEE');
