package org.athena.framework.data.jpa.interceptor;

import org.hibernate.EmptyInterceptor;

public class LogicalDeleteInterceptor extends EmptyInterceptor {

//    @Override
//    public String onPrepareStatement(String sql) {
//        if (sql.toLowerCase().startsWith("delete")) {
//            return sql.replaceFirst("delete from", "update")
//                    .replace(" where ", " set deleted = 1 where ");
//        }
//        return sql;
//    }
}

