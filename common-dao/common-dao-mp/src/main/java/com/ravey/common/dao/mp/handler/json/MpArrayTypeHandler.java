/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSON
 *  com.alibaba.fastjson.TypeReference
 *  com.alibaba.fastjson.parser.Feature
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.ibatis.type.BaseTypeHandler
 *  org.apache.ibatis.type.JdbcType
 */
package com.ravey.common.dao.mp.handler.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class MpArrayTypeHandler<T>
extends BaseTypeHandler<List<T>> {
    private final TypeReference<List<T>> typeReference;

    public MpArrayTypeHandler(Class<T> clazz) {
        this.typeReference = new TypeReference<List<T>>() {};
    }

    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    public void setParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, 12);
        } else {
            this.setNonNullParameter(ps, i, parameter, jdbcType);
        }
    }

    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            return this.parseResult(rs.getString(columnName));
        }
        catch (Exception e) {
            throw new SQLException("Error parsing JSON from column: " + columnName, e);
        }
    }

    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            return this.parseResult(rs.getString(columnIndex));
        }
        catch (Exception e) {
            throw new SQLException("Error parsing JSON from column index: " + columnIndex, e);
        }
    }

    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            return this.parseResult(cs.getString(columnIndex));
        }
        catch (Exception e) {
            throw new SQLException("Error parsing JSON from callable statement index: " + columnIndex, e);
        }
    }

    public List<T> parseResult(String json) {
        return StringUtils.isEmpty((CharSequence)json) ? null : (List)JSON.parseObject((String)json, this.typeReference, (Feature[])new Feature[0]);
    }
}