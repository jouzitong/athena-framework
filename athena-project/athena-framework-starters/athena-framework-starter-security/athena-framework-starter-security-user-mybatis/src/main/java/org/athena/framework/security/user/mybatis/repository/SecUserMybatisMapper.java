package org.athena.framework.security.user.mybatis.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.athena.framework.security.user.mybatis.entity.SecUserEntity;

import java.util.Optional;

@Mapper
public interface SecUserMybatisMapper {

    @Select("""
        SELECT id, username, display_name AS displayName, status, tenant_id AS tenantId
        FROM sec_user
        WHERE username = #{username}
        LIMIT 1
        """)
    Optional<SecUserEntity> findByUsername(@Param("username") String username);

    @Select("""
        SELECT id, username, display_name AS displayName, status, tenant_id AS tenantId
        FROM sec_user
        WHERE id = #{userId}
        LIMIT 1
        """)
    Optional<SecUserEntity> findByUserId(@Param("userId") Long userId);
}
