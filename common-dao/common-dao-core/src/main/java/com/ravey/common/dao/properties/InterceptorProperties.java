
package com.ravey.common.dao.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="common-dao.interceptor")
public class InterceptorProperties {
    public static final String PREFIX = "common-dao.interceptor";
    private boolean enableDataAuth = false;
    private boolean enableInjectUser = false;

    public boolean isEnableDataAuth() {
        return this.enableDataAuth;
    }

    public void setEnableDataAuth(boolean enableDataAuth) {
        this.enableDataAuth = enableDataAuth;
    }

    public boolean isEnableInjectUser() {
        return this.enableInjectUser;
    }

    public void setEnableInjectUser(boolean enableInjectUser) {
        this.enableInjectUser = enableInjectUser;
    }
}
