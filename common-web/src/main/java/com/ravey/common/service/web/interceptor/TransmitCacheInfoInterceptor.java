package com.ravey.common.service.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ravey.common.core.user.UserCache;
import com.ravey.common.core.user.UserInfo;
import com.ravey.common.service.web.result.ErrorResult;
import com.ravey.common.utils.json.JsonUtil;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TransmitCacheInfoInterceptor implements HandlerInterceptor {
    public TransmitCacheInfoInterceptor() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userInfoBase64 = request.getHeader("UserInfo");

        try {
            String clientInfo;
            if (userInfoBase64 != null) {
                clientInfo = new String(Base64.getDecoder().decode(userInfoBase64), StandardCharsets.UTF_8);
                UserCache.setUserInfo(JsonUtil.json2Bean(clientInfo, UserInfo.class));
            }
            return true;
        } catch (Exception var7) {
            Exception e = var7;
            this.setError(response, e);
            return false;
        }
    }

    public void setError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(500);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        ErrorResult er = new ErrorResult(-1, e.getMessage());
        response.getWriter().write(JsonUtil.bean2Json(er));
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserCache.clear();
    }
}
