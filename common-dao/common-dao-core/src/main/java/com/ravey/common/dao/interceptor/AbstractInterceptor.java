/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ravey.common.core.UserInfo
 *  com.ravey.common.core.UserInfoCache
 *  org.apache.ibatis.mapping.MappedStatement
 *  org.apache.ibatis.plugin.Interceptor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.ravey.common.dao.interceptor;

import com.ravey.common.core.user.UserInfo;
import com.ravey.common.core.user.UserCache;
import java.lang.annotation.Annotation;
import java.util.Objects;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractInterceptor
implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(AbstractInterceptor.class);

    protected abstract Annotation getAnnotation(MappedStatement var1);

    protected boolean isNextWithUserInfo() {
        UserInfo userInfo = UserCache.getUserInfo();
        return userInfo == null;
    }
}
