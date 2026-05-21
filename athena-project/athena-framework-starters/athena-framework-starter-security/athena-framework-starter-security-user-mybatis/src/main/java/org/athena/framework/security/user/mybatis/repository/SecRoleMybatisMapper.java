package org.athena.framework.security.user.mybatis.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.athena.framework.security.user.mybatis.entity.SecRoleEntity;

import java.util.Collection;
import java.util.List;

@Mapper
public interface SecRoleMybatisMapper {

    @Select({
        "<script>",
        "SELECT id, role_code AS roleCode, role_name AS roleName, status",
        "FROM sec_role",
        "WHERE role_code IN",
        "<foreach item='code' collection='roleCodes' open='(' separator=',' close=')'>",
        "#{code}",
        "</foreach>",
        "</script>"
    })
    List<SecRoleEntity> findByRoleCodeIn(@Param("roleCodes") Collection<String> roleCodes);
}
