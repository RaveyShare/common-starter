
package com.ravey.common.dao.interceptor;

import com.alibaba.fastjson.JSON;
import com.ravey.common.core.user.UserInfo;
import com.ravey.common.core.user.UserCache;
import com.ravey.common.dao.annotation.InjectUser;
import com.ravey.common.dao.interceptor.AbstractInterceptor;
import com.ravey.common.dao.utils.InterceptorUtil;
import com.ravey.common.dao.utils.ReflectUtil;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts(value={@Signature(method="update", type=Executor.class, args={MappedStatement.class, Object.class})})
public class InjectUserInterceptor
extends AbstractInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(InjectUserInterceptor.class);

    protected InjectUser getAnnotation(MappedStatement ms) {
        try {
            return InterceptorUtil.getAnnotation(ms, InjectUser.class);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement)args[0];
        if (!SqlCommandType.INSERT.equals((Object)ms.getSqlCommandType()) && !SqlCommandType.UPDATE.equals((Object)ms.getSqlCommandType())) {
            return invocation.proceed();
        }
        InjectUser injectUser = this.getAnnotation(ms);
        if (injectUser == null) {
            return invocation.proceed();
        }
        Object param = args[1] == null ? new HashMap(16) : args[1];
        Map<String, Object> baseEntity = this.buildBaseEntity(ms.getSqlCommandType());
        if (logger.isDebugEnabled()) {
            logger.debug("SQL入参修改前{}", (Object)JSON.toJSONString((Object)param));
        }
        if (param instanceof Map) {
            Map temp = (Map)param;
            this.replaceMapVal(baseEntity, temp, injectUser);
            args[1] = temp;
        } else {
            ReflectUtil.coverObj(baseEntity, param, injectUser.isForceInject());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("SQL入参修改后{}", (Object)JSON.toJSONString((Object)param));
        }
        return invocation.proceed();
    }

    private Map<String, Object> buildBaseEntity(SqlCommandType type) {
        UserInfo userInfo = UserCache.getUserInfo();
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setUserId("system");
            userInfo.setUsername("system");
        }
        HashMap<String, Object> baseMap = new HashMap<String, Object>(16);
        baseMap.put("updater", userInfo.getUsername());
        baseMap.put("updaterId", userInfo.getUserId());
        baseMap.put("updateTime", LocalDateTime.now());
        if (type.equals((Object)SqlCommandType.INSERT)) {
            baseMap.put("createTime", LocalDateTime.now());
            baseMap.put("creator", userInfo.getUsername());
            baseMap.put("creatorId", userInfo.getUserId());
        }
        return baseMap;
    }

    private void replaceMapVal(Map<String, Object> baseEntity, Map<String, Object> temp, InjectUser injectUser) throws IllegalAccessException {
        if (injectUser.paramNames().length > 0) {
            List<String> paramNames = Arrays.asList(injectUser.paramNames());
            for (Map.Entry<String, Object> entry : temp.entrySet()) {
                String k2 = entry.getKey();
                Object v2 = entry.getValue();
                if (!paramNames.contains(k2) || v2 == null) continue;
                ReflectUtil.coverObj(baseEntity, v2, injectUser.isForceInject());
                temp.put(k2, v2);
            }
        } else {
            baseEntity.forEach((k, v) -> {
                Object o = temp.get(k);
                if (o == null || injectUser.isForceInject()) {
                    temp.put((String)k, v);
                }
            });
        }
    }

    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap((Object)target, (Interceptor)this);
        }
        return target;
    }

    public void setProperties(Properties properties) {
    }
}
