package org.authena.mybatis.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.arthena.common.base.DBJson;
import org.arthena.common.utils.JacksonJsonUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * json 类型转换器
 *
 * @author zhouzhitong
 * @since 2022/9/25
 */
@Slf4j
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({DBJson.class})
public final class DefaultJsonHandler extends BaseTypeHandler<Object> {

    private final Class<?> type;

    public DefaultJsonHandler(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        String json = JacksonJsonUtils.writeValueAsString(parameter);
        ps.setString(i, json);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return processJson(json);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return processJson(json);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return processJson(json);
    }

    private Object processJson(String json) {
        return JacksonJsonUtils.readValue(json, type);
    }

}
