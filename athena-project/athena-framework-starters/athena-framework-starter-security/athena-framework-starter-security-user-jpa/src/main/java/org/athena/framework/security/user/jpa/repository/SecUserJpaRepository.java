package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.security.user.jpa.entity.SecUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户主数据 JPA 仓储。
 * 提供按用户名与用户 ID 查询用户主记录的能力。
 */
public interface SecUserJpaRepository extends JpaRepository<SecUserEntity, Long> {

    Optional<SecUserEntity> findByUsername(String username);

    Optional<SecUserEntity> findByUserId(String userId);
}
