package org.athena.framework.data.mybatis.interceptor;

import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.arthena.framework.common.service.IUserContextService;
import org.athena.framework.data.mybatis.builder.EmbeddedSqlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 自动插入的数据包括：创建人、创建时间、修改人、修改时间
 *
 * @author liao
 */
@Component
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class EmbeddedInterceptor implements Interceptor {

    @Autowired(required = false)
    private IUserContextService userContextService;

    private Configuration cfg;

    @Override
    public Object intercept(Invocation inv) throws Throwable {
        StatementHandler handler = (StatementHandler) inv.getTarget();
        MetaObject meta = SystemMetaObject.forObject(handler);
        // 从 delegate.mappedStatement.configuration 取
        cfg = (Configuration) meta.getValue("delegate.mappedStatement.configuration");

        BoundSql bs = handler.getBoundSql();
        Object param = bs.getParameterObject();
        if (param != null && hasEmbedded(param.getClass())) {
            FlatSql flatSql = EmbeddedSqlBuilder.rewrite(bs.getSql(), param);
            setSql(bs, flatSql);
        }
        return inv.proceed();
    }

    private void setSql(BoundSql bs, FlatSql flatSql) {
        if (flatSql == null) {
            return;
        }
        rewriteBoundSql(bs, flatSql.sql, flatSql.params);
//        try {
//            Field sqlField = BoundSql.class.getDeclaredField("sql");
//            Field additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
//            sqlField.setAccessible(true);
//            additionalParametersField.setAccessible(true);
//
//            sqlField.set(bs, flatSql.getSql());
//
//            // JacksonJsonUtils.toMap(flatSql.getParams());
//            Map<String, Object> paramsMap = ClassUtils.getFieldValue(bs, additionalParametersField);
//            paramsMap.putAll(flatSql.getParams());
////            Object parameter = paramsMap.get("_parameter");
////            Map<String, Object> map = JacksonJsonUtils.toMap(parameter);
////            map.putAll(flatSql.getParams());
////            paramsMap.put("_parameter", map);
////            additionalParametersField.set(bs, flatSql.getParams());
//
//            sqlField.setAccessible(false);
//            additionalParametersField.setAccessible(false);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException("无法通过反射修改 BoundSql 对象", e);
//        }
    }

    @SuppressWarnings("unchecked")
    public void rewriteBoundSql(BoundSql boundSql, String newSql, Map<String, Object> flatParams) {
        try {
            // 1. 改 SQL
            Field sqlField = BoundSql.class.getDeclaredField("sql");
            sqlField.setAccessible(true);
            sqlField.set(boundSql, newSql);

            // 2. 补 parameterMappings
            Field pmField = BoundSql.class.getDeclaredField("parameterMappings");
            pmField.setAccessible(true);
            List<ParameterMapping> pms = (List<ParameterMapping>) pmField.get(boundSql);

            flatParams.forEach((k, v) -> {
                boolean exists = pms.stream().anyMatch(pm -> pm.getProperty().equals(k));
                if (!exists) {
                    pms.add(new ParameterMapping.Builder(this.cfg, k, Object.class).build());
                }
            });

            // 3. 塞 additionalParameters
            setAdditionalParams(boundSql, flatParams);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void setAdditionalParams(BoundSql boundSql, Map<String, Object> params) {
        try {
            Field f = BoundSql.class.getDeclaredField("additionalParameters");
            f.setAccessible(true);
            Map<String, Object> exist = (Map<String, Object>) f.get(boundSql);

            if (exist != null) {
                exist.putAll(params);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot inject additionalParameters", e);
        }
    }


    private boolean hasEmbedded(Class<?> clazz) {
        // 判断 clazz 的字段是否 包含 @Embedded 注解
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Embedded.class)) {
                return true;
            }
        }
        return false;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FlatSql {
        private String sql;
        private Map<String, Object> params;
    }
}
