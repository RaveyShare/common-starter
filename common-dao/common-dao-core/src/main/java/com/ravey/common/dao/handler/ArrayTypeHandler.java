
package com.ravey.common.dao.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class ArrayTypeHandler<T>
implements TypeHandler<List<T>> {
    private final TypeReference<List<T>> typeReference;

    public ArrayTypeHandler(Class<T> clazz) {
        this.typeReference = new TypeReference<List<T>>() {};
    }

    public void setParameter(PreparedStatement ps, int i, List<T> t, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(t));
    }

    public List<T> getResult(ResultSet rs, String s) throws SQLException {
        return this.parseResult(rs.getString(s));
    }

    public List<T> getResult(ResultSet rs, int i) throws SQLException {
        return this.parseResult(rs.getString(i));
    }

    public List<T> getResult(CallableStatement cs, int i) throws SQLException {
        return this.parseResult(cs.getString(i));
    }

    public List<T> parseResult(String json) {
        return StringUtils.isEmpty((CharSequence)json) ? null : (List)JSON.parseObject((String)json, this.typeReference, (Feature[])new Feature[0]);
    }
}
