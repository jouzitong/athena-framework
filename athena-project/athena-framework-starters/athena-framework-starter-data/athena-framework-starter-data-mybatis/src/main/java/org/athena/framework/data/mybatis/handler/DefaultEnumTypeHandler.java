package org.athena.framework.data.mybatis.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.arthena.framework.common.enums.IEnum;
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
public final class DefaultEnumTypeHandler extends BaseTypeHandler<IEnum> {

    private final Class<? extends IEnum> type;

    public DefaultEnumTypeHandler(Class<? extends IEnum> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, IEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public IEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getBaseEnum(getNullableCode(rs, columnName));
    }

    @Override
    public IEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getBaseEnum(getNullableCode(rs, columnIndex));
    }

    @Override
    public IEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getBaseEnum(getNullableCode(cs, columnIndex));
    }

    private IEnum getBaseEnum(Integer code) {
        return EnumUtils.codeOf(type, code);
    }

    private Integer getNullableCode(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return rs.wasNull() ? null : code;
    }

    private Integer getNullableCode(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return rs.wasNull() ? null : code;
    }

    private Integer getNullableCode(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return cs.wasNull() ? null : code;
    }

}
