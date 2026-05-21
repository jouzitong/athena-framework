package org.athena.framework.security.user.mybatis.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.athena.framework.security.user.mybatis.entity.SecPermissionEntity;

import java.util.Collection;
import java.util.List;

@Mapper
public interface SecPermissionMybatisMapper {

    @Select({
        "<script>",
        "SELECT id, permission_code AS permissionCode, permission_name AS permissionName, status",
        "FROM sec_permission",
        "WHERE permission_code IN",
        "<foreach item='code' collection='permissionCodes' open='(' separator=',' close=')'>",
        "#{code}",
        "</foreach>",
        "</script>"
    })
    List<SecPermissionEntity> findByPermissionCodeIn(@Param("permissionCodes") Collection<String> permissionCodes);
}
