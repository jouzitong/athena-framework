package org.athena.framework.security.user.mybatis.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.athena.framework.security.user.mybatis.entity.SecAuditLogEntity;

@Mapper
public interface SecAuditLogMybatisMapper {

    @Insert("""
        INSERT INTO sec_audit_log
        (category, action, result, user_id, username, tenant_id, resource, detail, request_ip, attributes_json, occurred_at)
        VALUES
        (#{category}, #{action}, #{result}, #{userId}, #{username}, #{tenantId}, #{resource}, #{detail}, #{requestIp}, #{attributesJson}, #{occurredAt})
        """)
    int insert(SecAuditLogEntity entity);
}
