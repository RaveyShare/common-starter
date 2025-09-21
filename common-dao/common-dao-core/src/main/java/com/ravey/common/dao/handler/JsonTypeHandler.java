
package com.ravey.common.dao.handler;

import com.alibaba.fastjson.JSON;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class JsonTypeHandler<T>
implements TypeHandler<T> {
    private final Class<T> clazz;

    public JsonTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }

    public void setParameter(PreparedStatement ps, int i, T t, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(t));
    }

    public T getResult(ResultSet rs, String s) throws SQLException {
        return (T)JSON.parseObject((String)rs.getString(s), this.clazz);
    }

    public T getResult(ResultSet rs, int i) throws SQLException {
        return (T)JSON.parseObject((String)rs.getString(i), this.clazz);
    }

    public T getResult(CallableStatement cs, int i) throws SQLException {
        return (T)JSON.parseObject((String)cs.getString(i), this.clazz);
    }
}
