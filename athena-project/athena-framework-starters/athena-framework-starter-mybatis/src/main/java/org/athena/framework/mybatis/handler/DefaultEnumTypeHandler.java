package org.athena.framework.mybatis.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.arthena.framework.common.base.BaseEnum;
import org.arthena.framework.common.utils.EnumUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 枚举类型handler
 *
 * @author zhouzhitong
 * @since 2022/9/25
 */
@Slf4j
public final class DefaultEnumTypeHandler extends BaseTypeHandler<BaseEnum> {

    private final Class<? extends BaseEnum> type;

    public DefaultEnumTypeHandler(Class<? extends BaseEnum> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BaseEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public BaseEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return getBaseEnum(code);
    }

    @Override
    public BaseEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return getBaseEnum(code);
    }

    @Override
    public BaseEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return getBaseEnum(code);
    }

    private BaseEnum getBaseEnum(int code) {
        return EnumUtils.codeOf(type, code);
    }

}
