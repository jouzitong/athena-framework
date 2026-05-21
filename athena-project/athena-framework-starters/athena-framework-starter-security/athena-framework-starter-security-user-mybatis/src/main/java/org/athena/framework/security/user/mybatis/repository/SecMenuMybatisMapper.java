package org.athena.framework.security.user.mybatis.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.athena.framework.security.user.mybatis.entity.SecMenuEntity;

import java.util.List;

@Mapper
public interface SecMenuMybatisMapper {

    @Select("""
        SELECT id, menu_code AS menuCode, parent_code AS parentCode, menu_name AS menuName,
               path, component, permission_code AS permissionCode, sort_order AS sortOrder, status
        FROM sec_menu
        ORDER BY sort_order ASC, id ASC
        """)
    List<SecMenuEntity> findAllByOrderBySortOrderAscIdAsc();
}
