package org.athena.framework.security.user.mybatis.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.athena.framework.security.user.mybatis.entity.SecUserCredentialEntity;

import java.util.Optional;

@Mapper
public interface SecUserCredentialMybatisMapper {

    @Select("""
        SELECT id, user_id AS userId, credential_type AS credentialType,
               password_hash AS passwordHash, password_algo AS passwordAlgo, password_salt AS passwordSalt
        FROM sec_user_credential
        WHERE user_id = #{userId} AND credential_type = #{credentialType}
        ORDER BY id ASC
        LIMIT 1
        """)
    Optional<SecUserCredentialEntity> findFirstByUserIdAndCredentialType(@Param("userId") String userId,
                                                                          @Param("credentialType") String credentialType);
}
