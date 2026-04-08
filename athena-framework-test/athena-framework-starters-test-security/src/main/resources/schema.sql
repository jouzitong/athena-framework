DROP TABLE IF EXISTS sec_user_credential;
DROP TABLE IF EXISTS sec_user;

CREATE TABLE sec_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id VARCHAR(64) NOT NULL UNIQUE,
  username VARCHAR(64) NOT NULL,
  display_name VARCHAR(128),
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED',
  tenant_id VARCHAR(64)
);

CREATE TABLE sec_user_credential (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id VARCHAR(64) NOT NULL,
  credential_type VARCHAR(32) NOT NULL DEFAULT 'PASSWORD',
  password_hash VARCHAR(255) NOT NULL,
  password_algo VARCHAR(32) NOT NULL,
  password_salt VARCHAR(255),
  CONSTRAINT uk_user_credential UNIQUE(user_id, credential_type)
);
