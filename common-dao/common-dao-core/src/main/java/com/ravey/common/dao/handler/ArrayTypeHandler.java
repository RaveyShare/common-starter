
package com.ravey.common.dao.handler;

import com.ravey.common.utils.json.JsonUtil;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class ArrayTypeHandler<T> implements TypeHandler<List<T>> {
    private final Class<T> clazz;

    public ArrayTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JsonUtil.bean2Json(parameter));
    }

    @Override
    public List<T> getResult(ResultSet rs, String columnName) throws SQLException {
        return this.parseResult(rs.getString(columnName));
    }

    @Override
    public List<T> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.parseResult(rs.getString(columnIndex));
    }

    @Override
    public List<T> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.parseResult(cs.getString(columnIndex));
    }

    public List<T> parseResult(String json) {
        return StringUtils.isEmpty(json) ? null : JsonUtil.json2ListBean(json, this.clazz);
    }
}
