
package com.ravey.common.dao.interceptor;


import com.ravey.common.core.user.UserInfo;
import com.ravey.common.core.user.UserCache;
import com.ravey.common.dao.annotation.DataAuth;
import com.ravey.common.dao.annotation.Element;

import com.ravey.common.dao.interceptor.AbstractDataAuthInterceptor;
import com.ravey.common.dao.interceptor.SimpleDataAuthInterceptor;
import com.ravey.common.dao.utils.InterceptorUtil;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Properties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

@Intercepts(value={@Signature(method="query", type=Executor.class, args={MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class SimpleDataAuthInterceptor
extends AbstractDataAuthInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(SimpleDataAuthInterceptor.class);

    protected DataAuth getAnnotation(MappedStatement ms) {
        try {
            DataAuth dataAuth = InterceptorUtil.getAnnotation(ms, DataAuth.class);
            if (dataAuth == null || dataAuth.value().length == 0) {
                return null;
            }
            return dataAuth;
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String appendElements(Annotation annotation) {
        DataAuth dataAuth = (DataAuth)annotation;
        Element[] elements = dataAuth.value();
        StringBuilder sqlBuilder = new StringBuilder("");
        UserInfo userInfo = UserCache.getUserInfo();
        // 由于相关权限字段已被删除，此处暂时不进行权限过滤
        // 如需要权限控制，请重新实现相关逻辑
        return sqlBuilder.toString();
    }

    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap((Object)target, (Interceptor)this);
        }
        return target;
    }

    public void setProperties(Properties properties) {
    }

    private boolean isUnGlobal(List<Long> ids) {
        return CollectionUtils.isEmpty(ids) || !ids.contains(0L);
    }
}
