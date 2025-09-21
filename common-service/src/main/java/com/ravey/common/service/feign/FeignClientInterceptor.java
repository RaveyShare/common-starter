package com.ravey.common.service.feign;

import com.alibaba.fastjson.JSON;
import com.ravey.common.core.user.UserCache;
import com.ravey.common.core.user.UserInfo;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FeignClientInterceptor implements RequestInterceptor {
    public FeignClientInterceptor() {
    }

    public void apply(RequestTemplate requestTemplate) {
        UserInfo userInfo = UserCache.getUserInfo();
        if (userInfo != null) {
            String userInfoStr = Base64.getEncoder().encodeToString(JSON.toJSONString(userInfo).getBytes(StandardCharsets.UTF_8));
            requestTemplate.header("UserInfo", new String[]{userInfoStr});
        }

    }
}
