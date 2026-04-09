INSERT INTO sec_user(user_id, username, display_name, status, tenant_id)
VALUES
  ('U1001', 'admin', 'Admin User', 'ENABLED', NULL),
  ('U1002', 'operator', 'Operator User', 'ENABLED', NULL),
  ('U1003', 'guest', 'Guest User', 'ENABLED', NULL);

INSERT INTO sec_user_credential(user_id, credential_type, password_hash, password_algo, password_salt)
VALUES
  ('U1001', 'PASSWORD', 'admin123', 'PLAINTEXT', NULL),
  ('U1002', 'PASSWORD', 'op123', 'PLAINTEXT', NULL),
  ('U1003', 'PASSWORD', 'guest123', 'PLAINTEXT', NULL);

INSERT INTO sec_role(role_code, role_name, status)
VALUES
  ('ADMIN', 'Administrator', 'ENABLED'),
  ('OPERATOR', 'Operator', 'ENABLED'),
  ('GUEST', 'Guest', 'ENABLED');

INSERT INTO sec_permission(permission_code, permission_name, status)
VALUES
  ('menu:view', 'View dashboard menu', 'ENABLED'),
  ('user:read', 'Read users', 'ENABLED'),
  ('user:create', 'Create users', 'ENABLED');

INSERT INTO sec_user_role(user_id, role_code)
VALUES
  ('U1001', 'ADMIN'),
  ('U1002', 'OPERATOR'),
  ('U1003', 'GUEST');

INSERT INTO sec_role_permission(role_code, permission_code)
VALUES
  ('ADMIN', 'menu:view'),
  ('ADMIN', 'user:read'),
  ('ADMIN', 'user:create'),
  ('OPERATOR', 'menu:view'),
  ('OPERATOR', 'user:read');

INSERT INTO sec_menu(menu_code, parent_code, menu_name, path, component, permission_code, sort_order, status)
VALUES
  ('MENU_DASHBOARD', NULL, 'Dashboard', '/dashboard', 'dashboard/index', 'menu:view', 10, 'ENABLED'),
  ('MENU_USER', NULL, 'Users', '/users', 'user/index', 'user:read', 20, 'ENABLED'),
  ('MENU_USER_CREATE', 'MENU_USER', 'Create User', '/users/create', 'user/create', 'user:create', 30, 'ENABLED');
