package com.ravey.common.object.store.aliyun.autoconfigure;

import com.ravey.common.object.store.aliyun.properties.AliyunObjectStoreProperties;
import com.ravey.common.object.store.aliyun.template.AliyunObjectStoreTemplate;
import com.ravey.common.object.store.template.ObjectStoreTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({AliyunObjectStoreProperties.class})
@Configuration
public class AliyunObjectStoreAutoConfiguration {
    private final AliyunObjectStoreProperties properties;

    public AliyunObjectStoreAutoConfiguration(AliyunObjectStoreProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
        name = {"object.store.aliyun.endpoint", "object.store.aliyun.access-key-id", "object.store.aliyun.access-key-secret", "object.store.aliyun.bucket-name"}
    )
    public ObjectStoreTemplate aliyunObjectStoreTemplate() {
        return new AliyunObjectStoreTemplate(this.properties);
    }
}
