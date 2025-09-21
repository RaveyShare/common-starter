/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSON
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.ibatis.type.BaseTypeHandler
 *  org.apache.ibatis.type.JdbcType
 */
package com.ravey.common.dao.mp.handler.json;

import com.alibaba.fastjson.JSON;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class MpJsonTypeHandler<T>
extends BaseTypeHandler<T> {
    private final Class<T> clazz;

    public MpJsonTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }

    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, 12);
        } else {
            this.setNonNullParameter(ps, i, parameter, jdbcType);
        }
    }

    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.parseResult(rs.getString(columnName));
    }

    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.parseResult(rs.getString(columnIndex));
    }

    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.parseResult(cs.getString(columnIndex));
    }

    public T parseResult(String json) {
        return (T)(StringUtils.isEmpty((CharSequence)json) ? null : JSON.parseObject((String)json, this.clazz));
    }
}