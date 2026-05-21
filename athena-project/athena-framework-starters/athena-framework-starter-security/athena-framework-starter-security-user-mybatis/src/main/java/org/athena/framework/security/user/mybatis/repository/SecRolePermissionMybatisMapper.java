package org.athena.framework.security.user.mybatis.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.athena.framework.security.user.mybatis.entity.SecRolePermissionEntity;

import java.util.Collection;
import java.util.List;

@Mapper
public interface SecRolePermissionMybatisMapper {

    @Select({
        "<script>",
        "SELECT id, role_code AS roleCode, permission_code AS permissionCode",
        "FROM sec_role_permission",
        "WHERE role_code IN",
        "<foreach item='code' collection='roleCodes' open='(' separator=',' close=')'>",
        "#{code}",
        "</foreach>",
        "</script>"
    })
    List<SecRolePermissionEntity> findByRoleCodeIn(@Param("roleCodes") Collection<String> roleCodes);
}
