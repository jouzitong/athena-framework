package org.athena.framework.security.user.mybatis.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.athena.framework.security.user.mybatis.entity.SecUserRoleEntity;

import java.util.List;

@Mapper
public interface SecUserRoleMybatisMapper {

    @Select("""
        SELECT id, user_id AS userId, role_code AS roleCode
        FROM sec_user_role
        WHERE user_id = #{userId}
        """)
    List<SecUserRoleEntity> findByUserId(@Param("userId") Long userId);
}
