/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.ibatis.mapping.MappedStatement
 *  org.apache.ibatis.mapping.MappedStatement$Builder
 *  org.apache.ibatis.mapping.SqlSource
 */
package com.ravey.common.dao.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

public class InterceptorUtil {
    public static <T extends Annotation> T getAnnotation(MappedStatement ms, Class<T> clazz) throws ClassNotFoundException {
        String sid = ms.getId();
        String className = sid.substring(0, sid.lastIndexOf("."));
        String methodName = sid.substring(sid.lastIndexOf(".") + 1);
        Class<?> cla = Class.forName(className);
        Method[] methods = cla.getMethods();
        Optional<Method> optional = Arrays.stream(methods).filter(e -> e.getName().equals(methodName) && e.isAnnotationPresent(clazz)).findFirst();
        return (T)((Annotation)optional.map(m -> m.getAnnotation(clazz)).orElse(null));
    }

    public static MappedStatement newMappedStatement(MappedStatement ms, SqlSource sqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), sqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource()).fetchSize(ms.getFetchSize()).statementType(ms.getStatementType()).keyGenerator(ms.getKeyGenerator());
        String[] keyPropertyArr = ms.getKeyProperties();
        if (keyPropertyArr != null && keyPropertyArr.length > 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : keyPropertyArr) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout()).parameterMap(ms.getParameterMap()).resultMaps(ms.getResultMaps()).resultSetType(ms.getResultSetType()).cache(ms.getCache()).flushCacheRequired(ms.isFlushCacheRequired()).useCache(ms.isUseCache());
        return builder.build();
    }
}
