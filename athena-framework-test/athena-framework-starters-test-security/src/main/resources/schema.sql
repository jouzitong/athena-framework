DROP TABLE IF EXISTS sec_user_credential;
DROP TABLE IF EXISTS sec_user;
DROP TABLE IF EXISTS sec_user_role;
DROP TABLE IF EXISTS sec_role_permission;
DROP TABLE IF EXISTS sec_role;
DROP TABLE IF EXISTS sec_permission;
DROP TABLE IF EXISTS sec_menu;
DROP TABLE IF EXISTS sec_audit_log;

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

CREATE TABLE sec_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(64) NOT NULL UNIQUE,
  role_name VARCHAR(128) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED'
);

CREATE TABLE sec_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  permission_code VARCHAR(128) NOT NULL UNIQUE,
  permission_name VARCHAR(128) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED'
);

CREATE TABLE sec_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id VARCHAR(64) NOT NULL,
  role_code VARCHAR(64) NOT NULL,
  CONSTRAINT uk_user_role UNIQUE(user_id, role_code)
);

CREATE TABLE sec_role_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(64) NOT NULL,
  permission_code VARCHAR(128) NOT NULL,
  CONSTRAINT uk_role_permission UNIQUE(role_code, permission_code)
);

CREATE TABLE sec_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  menu_code VARCHAR(64) NOT NULL UNIQUE,
  parent_code VARCHAR(64),
  menu_name VARCHAR(128) NOT NULL,
  path VARCHAR(255),
  component VARCHAR(255),
  permission_code VARCHAR(128),
  sort_order INT NOT NULL DEFAULT 0,
  status VARCHAR(16) NOT NULL DEFAULT 'ENABLED'
);

CREATE TABLE sec_audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category VARCHAR(32) NOT NULL,
  action VARCHAR(64) NOT NULL,
  result VARCHAR(16) NOT NULL,
  user_id VARCHAR(64),
  username VARCHAR(64),
  tenant_id VARCHAR(64),
  resource VARCHAR(255),
  detail VARCHAR(500),
  request_ip VARCHAR(64),
  attributes_json VARCHAR(2000),
  occurred_at DATETIME(6) NOT NULL
);
