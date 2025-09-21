//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ravey.common.redis.autoconfigure;

import com.ravey.common.redis.aop.CustomLockAop;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
@AutoConfigureAfter({SlaveRedisConfiguration.class})
public class CustomLockConfiguration implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public CustomLockConfiguration() {
    }

    @Bean
    public CustomLockAop customLockAop() {
        return new CustomLockAop();
    }

    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        CustomLockConfiguration.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(String beanName) {
        return (T) applicationContext.getBean(beanName);

    }
}
