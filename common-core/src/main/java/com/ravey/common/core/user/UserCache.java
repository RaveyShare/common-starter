package com.ravey.common.core.user;

public class UserCache {

    private static final ThreadLocal<UserInfo> USER = new ThreadLocal();

    public UserCache() {
    }

    public static UserInfo getUserInfo() {
        return (UserInfo) USER.get();
    }

    public static UserInfo getNoNullUserInfo() {
        UserInfo userInfo = (UserInfo) USER.get();
        if (userInfo == null) {
            throw new RuntimeException("用户信息为空");
        } else {
            return userInfo;
        }
    }

    public static void setUserInfo(UserInfo userInfo) {
        USER.set(userInfo);
    }

    public static void clear() {
        USER.remove();
    }
}