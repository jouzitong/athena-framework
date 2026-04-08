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
