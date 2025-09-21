/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.ibatis.mapping.BoundSql
 *  org.apache.ibatis.mapping.SqlSource
 */
package com.ravey.common.dao.interceptor;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

public class BoundSqlSource implements SqlSource {
    private final BoundSql boundSql;

    public BoundSqlSource(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    public BoundSql getBoundSql(Object parameterObject) {
        return this.boundSql;
    }
}
