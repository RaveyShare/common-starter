/*
 * Decompiled with CFR 0.152.
 */
package com.ravey.common.dao.mp.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.ravey.common.dao.mp.handler.MybatisPlusObjectHandler;
import com.ravey.common.dao.mp.properties.RaveyMybatisPlusProperties;
import java.util.Objects;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {
    @Autowired
    private RaveyMybatisPlusProperties properties;

    @ConditionalOnMissingBean(value={MybatisPlusInterceptor.class})
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        RaveyMybatisPlusProperties.Plugin plugin = this.properties.getPlugin();
        if (Objects.nonNull(plugin)) {
            if (plugin.isEnableDynamicTableNameInnerInterceptor()) {
                interceptor.addInnerInterceptor((InnerInterceptor)this.getDynamicTableNameInnerInterceptor());
            }
            if (plugin.isEnablePaginationInterceptor()) {
                interceptor.addInnerInterceptor((InnerInterceptor)new PaginationInnerInterceptor(DbType.MYSQL));
            }
            if (plugin.isEnableOptimisticLockerInterceptor()) {
                interceptor.addInnerInterceptor((InnerInterceptor)new OptimisticLockerInnerInterceptor());
            }
        }
        return interceptor;
    }

    @ConditionalOnMissingBean(value={ConfigurationCustomizer.class})
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return i -> i.setObjectWrapperFactory((ObjectWrapperFactory)new MybatisMapWrapperFactory());
    }

    @ConditionalOnProperty(value={"mybatis-plus.enableMybatisPlusObjectHandler"}, havingValue="true", matchIfMissing=true)
    @Bean
    public MybatisPlusObjectHandler mybatisPlusObjectHandler() {
        return new MybatisPlusObjectHandler(this.properties);
    }

    @ConditionalOnProperty(value={"mybatis-plus.plugin.enableDynamicTableNameInnerInterceptor"}, havingValue="true", matchIfMissing=true)
    @Bean
    public DynamicTableNameInnerInterceptor getDynamicTableNameInnerInterceptor() {
        return new DynamicTableNameInnerInterceptor();
    }
}