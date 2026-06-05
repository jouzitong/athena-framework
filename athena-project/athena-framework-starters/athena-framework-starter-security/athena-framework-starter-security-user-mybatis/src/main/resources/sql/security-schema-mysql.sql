-- Athena Security MyBatis MySQL 初始化脚本
-- 适用模块: athena-framework-starter-security-user-mybatis

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `sec_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(64) NOT NULL COMMENT '登录用户名',
  `display_name` VARCHAR(128) DEFAULT NULL COMMENT '展示名称',
  `status` VARCHAR(16) NOT NULL COMMENT '用户状态',
  `tenant_id` VARCHAR(64) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_sec_user_username` (`username`),
  KEY `idx_sec_user_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户主表';

CREATE TABLE IF NOT EXISTS `sec_user_credential` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户主键ID',
  `credential_type` VARCHAR(32) NOT NULL COMMENT '凭据类型',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值',
  `password_algo` VARCHAR(32) DEFAULT NULL COMMENT '密码算法',
  `password_salt` VARCHAR(255) DEFAULT NULL COMMENT '密码盐值',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sec_user_credential_user_type` (`user_id`, `credential_type`),
  KEY `idx_sec_user_credential_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户凭据表';

CREATE TABLE IF NOT EXISTS `sec_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码',
  `role_name` VARCHAR(128) NOT NULL COMMENT '角色名称',
  `status` VARCHAR(16) NOT NULL COMMENT '角色状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sec_role_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

CREATE TABLE IF NOT EXISTS `sec_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `permission_code` VARCHAR(128) NOT NULL COMMENT '权限编码',
  `permission_name` VARCHAR(128) NOT NULL COMMENT '权限名称',
  `status` VARCHAR(16) NOT NULL COMMENT '权限状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sec_permission_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';

CREATE TABLE IF NOT EXISTS `sec_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户主键ID',
  `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sec_user_role_user_role` (`user_id`, `role_code`),
  KEY `idx_sec_user_role_user_id` (`user_id`),
  KEY `idx_sec_user_role_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

CREATE TABLE IF NOT EXISTS `sec_role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码',
  `permission_code` VARCHAR(128) NOT NULL COMMENT '权限编码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sec_role_permission_role_perm` (`role_code`, `permission_code`),
  KEY `idx_sec_role_permission_role_code` (`role_code`),
  KEY `idx_sec_role_permission_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';

CREATE TABLE IF NOT EXISTS `sec_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `menu_code` VARCHAR(64) NOT NULL COMMENT '菜单编码',
  `parent_code` VARCHAR(64) DEFAULT NULL COMMENT '父级菜单编码',
  `menu_name` VARCHAR(128) NOT NULL COMMENT '菜单名称',
  `path` VARCHAR(255) DEFAULT NULL COMMENT '路由路径',
  `component` VARCHAR(255) DEFAULT NULL COMMENT '前端组件路径',
  `permission_code` VARCHAR(128) DEFAULT NULL COMMENT '关联权限编码',
  `sort_order` INT DEFAULT NULL COMMENT '排序值',
  `status` VARCHAR(16) NOT NULL COMMENT '菜单状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sec_menu_menu_code` (`menu_code`),
  KEY `idx_sec_menu_parent_code` (`parent_code`),
  KEY `idx_sec_menu_permission_code` (`permission_code`),
  KEY `idx_sec_menu_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单表';

CREATE TABLE IF NOT EXISTS `sec_audit_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category` VARCHAR(32) NOT NULL COMMENT '日志分类',
  `action` VARCHAR(64) NOT NULL COMMENT '操作动作',
  `result` VARCHAR(16) NOT NULL COMMENT '操作结果',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户主键ID',
  `username` VARCHAR(64) DEFAULT NULL COMMENT '用户名',
  `tenant_id` VARCHAR(64) DEFAULT NULL COMMENT '租户ID',
  `resource` VARCHAR(255) DEFAULT NULL COMMENT '资源标识',
  `detail` VARCHAR(500) DEFAULT NULL COMMENT '详情描述',
  `request_ip` VARCHAR(64) DEFAULT NULL COMMENT '请求IP地址',
  `attributes_json` VARCHAR(2000) DEFAULT NULL COMMENT '扩展属性JSON',
  `occurred_at` DATETIME(3) NOT NULL COMMENT '发生时间',
  PRIMARY KEY (`id`),
  KEY `idx_sec_audit_log_occurred_at_id` (`occurred_at`, `id`),
  KEY `idx_sec_audit_log_user_id` (`user_id`),
  KEY `idx_sec_audit_log_category_action` (`category`, `action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审计日志表';

SET FOREIGN_KEY_CHECKS = 1;
