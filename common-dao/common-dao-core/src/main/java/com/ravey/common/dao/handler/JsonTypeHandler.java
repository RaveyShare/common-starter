
package com.ravey.common.dao.handler;

import com.ravey.common.utils.json.JsonUtil;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;
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
        ps.setString(i, JsonUtil.bean2Json(t));
    }

    public T getResult(ResultSet rs, String s) throws SQLException {
        String json = rs.getString(s);
        return StringUtils.isEmpty(json) ? null : JsonUtil.json2Bean(json, this.clazz);
    }

    public T getResult(ResultSet rs, int i) throws SQLException {
        String json = rs.getString(i);
        return StringUtils.isEmpty(json) ? null : JsonUtil.json2Bean(json, this.clazz);
    }

    public T getResult(CallableStatement cs, int i) throws SQLException {
        String json = cs.getString(i);
        return StringUtils.isEmpty(json) ? null : JsonUtil.json2Bean(json, this.clazz);
    }
}
