package com.ravey.common.service.web.aop;


import com.ravey.common.core.user.UserCache;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

@Order(100)
@Aspect
public class UserCacheClearAop {
    public UserCacheClearAop() {
    }

    @Pointcut("within(com.ravey..*Controller)")
    public void pointCut() {
    }

    @After("pointCut()")
    public void clear() {
        UserCache.clear();
    }
}

