
package com.ravey.common.dao.autoconfigure;

import com.ravey.common.dao.interceptor.InjectUserInterceptor;
import com.ravey.common.dao.interceptor.SimpleDataAuthInterceptor;
import com.ravey.common.dao.properties.InterceptorProperties;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(value={InterceptorProperties.class})
@Configuration
@AutoConfigureAfter(value={PageHelperAutoConfiguration.class})
@ConditionalOnBean(value={SqlSessionFactory.class})
public class InterceptAutoConfiguration {
    private final InterceptorProperties interceptorProperties;
    private final List<SqlSessionFactory> sqlSessionFactoryList;

    public InterceptAutoConfiguration(InterceptorProperties interceptorProperties, List<SqlSessionFactory> sqlSessionFactoryList) {
        this.interceptorProperties = interceptorProperties;
        this.sqlSessionFactoryList = sqlSessionFactoryList;
    }

    @PostConstruct
    public void addMybatisIntercept() {
        SimpleDataAuthInterceptor simpleDataAuthInterceptor = new SimpleDataAuthInterceptor();
        InjectUserInterceptor injectUserInterceptor = new InjectUserInterceptor();
        for (SqlSessionFactory sqlSessionFactory : this.sqlSessionFactoryList) {
            if (this.interceptorProperties.isEnableDataAuth()) {
                sqlSessionFactory.getConfiguration().addInterceptor((Interceptor)simpleDataAuthInterceptor);
            }
            if (!this.interceptorProperties.isEnableInjectUser()) continue;
            sqlSessionFactory.getConfiguration().addInterceptor((Interceptor)injectUserInterceptor);
        }
    }
}
