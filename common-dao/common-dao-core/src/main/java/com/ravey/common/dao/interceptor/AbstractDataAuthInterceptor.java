
package com.ravey.common.dao.interceptor;

import com.ravey.common.dao.annotation.Element;
import com.ravey.common.dao.interceptor.AbstractInterceptor;
import com.ravey.common.dao.interceptor.BoundSqlSource;
import com.ravey.common.dao.utils.InterceptorUtil;
import com.ravey.common.dao.utils.SqlUtil;
import java.lang.annotation.Annotation;
import java.util.List;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractDataAuthInterceptor
extends AbstractInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDataAuthInterceptor.class);

    public Object intercept(Invocation invocation) throws Throwable {
        if (this.isNextWithUserInfo()) {
            return invocation.proceed();
        }
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement)args[0];
        if (!SqlCommandType.SELECT.equals((Object)ms.getSqlCommandType())) {
            return invocation.proceed();
        }
        Annotation annotation = this.getAnnotation(ms);
        if (annotation == null) {
            return invocation.proceed();
        }
        Object param = args[1];
        BoundSql boundSql = ms.getBoundSql(param);
        String originSql = boundSql.getSql();
        if (logger.isDebugEnabled()) {
            logger.debug("原始sql:{}", (Object)originSql);
        }
        String newSql = this.handleSql(originSql, annotation);
        if (logger.isDebugEnabled()) {
            logger.debug("拦截后sql:{}", (Object)newSql);
        }
        MappedStatement nms = InterceptorUtil.newMappedStatement(ms, new BoundSqlSource(boundSql));
        MetaObject msObject = MetaObject.forObject((Object)nms, (ObjectFactory)new DefaultObjectFactory(), (ObjectWrapperFactory)new DefaultObjectWrapperFactory(), (ReflectorFactory)new DefaultReflectorFactory());
        msObject.setValue("sqlSource.boundSql.sql", (Object)newSql);
        args[0] = nms;
        return invocation.proceed();
    }

    protected String handleSql(String originSql, Annotation annotation) {
        boolean flag;
        String upSql = ((String)originSql).toUpperCase();
        int index = SqlUtil.findKeyWordIndex(upSql);
        String lastSql = "";
        if (index > -1) {
            lastSql = ((String)originSql).substring(index);
            originSql = ((String)originSql).substring(0, index);
        }
        if (!(flag = upSql.contains("WHERE"))) {
            originSql = (String)originSql + " WHERE 1 = 1 ";
        }
        return (String)originSql + " " + this.appendElements(annotation) + " " + lastSql;
    }

    protected abstract String appendElements(Annotation var1);

    protected <T> void buildElementStr(StringBuilder sqlBuilder, Element element, List<T> ids) {
        String column = element.column();
        String tableAlias = element.tableAlias();
        Object sqlTableAlias = StringUtils.hasText((String)tableAlias) ? tableAlias + "." : "";
        String authType = element.authType();
        if (!StringUtils.hasText((String)element.column())) {
            // Default column mapping based on auth type
            switch (authType) {
                case "SHOP":
                    column = "shop_id";
                    break;
                case "WAREHOUSE":
                    column = "warehouse_id";
                    break;
                case "SUPPLIER":
                    column = "supplier_id";
                    break;
                case "APP":
                    column = "app_id";
                    break;
                case "CHANNEL":
                    column = "channel_id";
                    break;
                default:
                    column = "id";
                    break;
            }
        }
        Object sqlColumnName = column;
        boolean isHbase = "HBASE".equals(element.dataSourceType());
        if (isHbase) {
            sqlColumnName = "\"" + column + "\"";
        }
        sqlColumnName = (String)sqlTableAlias + (String)sqlColumnName;
        sqlBuilder.append(" AND ");
        if (element.ignoreNull()) {
            sqlBuilder.append("IFNULL(").append((String)sqlColumnName).append(",-1)");
        } else {
            sqlBuilder.append((String)sqlColumnName);
        }
        sqlBuilder.append(" IN (");
        if (!CollectionUtils.isEmpty(ids)) {
            ids.forEach(e -> sqlBuilder.append(e).append(","));
        }
        sqlBuilder.append("-1)");
    }
}
