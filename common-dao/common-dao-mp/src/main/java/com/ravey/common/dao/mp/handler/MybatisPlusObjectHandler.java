/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ravey.common.core.UserInfo
 *  com.ravey.common.core.UserInfoCache
 *  com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
 *  org.apache.ibatis.reflection.MetaObject
 */
package com.ravey.common.dao.mp.handler;


import com.ravey.common.core.user.UserInfo;
import com.ravey.common.core.user.UserCache;
import com.ravey.common.dao.mp.properties.RaveyMybatisPlusProperties;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import org.apache.ibatis.reflection.MetaObject;

public class MybatisPlusObjectHandler
implements MetaObjectHandler {
    private final RaveyMybatisPlusProperties properties;

    public MybatisPlusObjectHandler(RaveyMybatisPlusProperties properties) {
        this.properties = properties;
    }

    public void insertFill(MetaObject metaObject) {
        String userId = this.getUserId();
        Object userName = this.getUserName();
        String jobNumber = this.getJobNumber();
        if (this.properties.isEnableInjectUserWithJobNumber() && jobNumber != null && !jobNumber.isEmpty()) {
            userName = this.getUserName() + " | " + jobNumber;
        }
        this.fillStrategy(metaObject, "creator", userName);
        this.fillStrategy(metaObject, "creatorId", userId);
        this.fillStrategy(metaObject, "createTime", LocalDateTime.now());
        this.fillStrategy(metaObject, "updater", userName);
        this.fillStrategy(metaObject, "updaterId", userId);
        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
    }

    public void updateFill(MetaObject metaObject) {
        String userId = this.getUserId();
        Object userName = this.getUserName();
        String jobNumber = this.getJobNumber();
        if (this.properties.isEnableInjectUserWithJobNumber() && jobNumber != null && !jobNumber.isEmpty()) {
            userName = this.getUserName() + " | " + jobNumber;
        }
        metaObject.setValue("updater", null);
        this.strictUpdateFill(metaObject, "updater", String.class, (String) userName);
        metaObject.setValue("updaterId", null);
        this.strictUpdateFill(metaObject, "updaterId", String.class, userId);
        metaObject.setValue("updateTime", null);
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    private String getUserName() {
        UserInfo userInfo = UserCache.getUserInfo();
        if (userInfo == null) {
            return "系统";
        }
        return userInfo.getUsername();
    }

    private String getJobNumber() {
        UserInfo userInfo = UserCache.getUserInfo();
        if (userInfo == null) {
            return null;
        }
        // UserInfo 类中没有 getJobNumber() 方法，暂时返回 null
        return null;
    }

    private String getUserId() {
        UserInfo userInfo = UserCache.getUserInfo();
        if (userInfo == null) {
            return "system";
        }
        return userInfo.getUserId();
    }
}