//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ravey.common.redis.autoconfigure;

import com.ravey.common.redis.CallManage;
import com.ravey.common.redis.Generator;
import com.ravey.common.redis.LockHandler;
import com.ravey.common.redis.properties.CustomRedisGroupProperties;
import com.ravey.common.redis.properties.CustomRedisProperties;
import io.lettuce.core.resource.ClientResources;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({CustomRedisGroupProperties.class})
@AutoConfigureBefore({RedisAutoConfiguration.class})
@Configuration
public class SlaveRedisConfiguration extends RedisConnectionConfiguration implements BeanFactoryAware {
    private final Map<String, CustomRedisProperties> slaveRedisMap;
    public static final String REDIS_TEMPLATE = "RedisTemplate";
    public static final String STRING_REDIS_TEMPLATE = "StringRedisTemplate";
    private final ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers;
    private final ClientResources clientResources;
    boolean isSalveInit = false;

    public SlaveRedisConfiguration(CustomRedisGroupProperties bcRedisGroupProperties, ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources) throws Exception {
        if (Objects.nonNull(bcRedisGroupProperties.getSlaveRedisList())) {
            boolean result = bcRedisGroupProperties.getSlaveRedisList().stream().anyMatch((x) -> {
                return !StringUtils.hasText(x.getClientName());
            });
            if (result) {
                throw new Exception("spring.slave-redis-list.client-name 不能为空");
            }

            Set<String> clientNameSet = (Set)bcRedisGroupProperties.getSlaveRedisList().stream().map(RedisProperties::getClientName).collect(Collectors.toSet());
            if (clientNameSet.size() != bcRedisGroupProperties.getSlaveRedisList().size()) {
                throw new Exception("spring.slave-redis-list.client-name 不能重复");
            }

            this.slaveRedisMap = (Map)bcRedisGroupProperties.getSlaveRedisList().stream().collect(Collectors.toMap(RedisProperties::getClientName, Function.identity()));
        } else {
            this.slaveRedisMap = new HashMap();
        }

        this.builderCustomizers = builderCustomizers;
        this.clientResources = clientResources;
    }

    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        if (!this.isSalveInit) {
            if (!this.slaveRedisMap.isEmpty()) {
                DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory)beanFactory;
                this.slaveRedisMap.forEach((key, properties) -> {
                    BeanDefinitionBuilder connectionBuilder = BeanDefinitionBuilder.rootBeanDefinition(LettuceConnectionFactory.class);
                    LettuceClientConfiguration clientConfig = this.getLettuceClientConfiguration(this.builderCustomizers, this.clientResources, properties);
                    RedisSentinelConfiguration sentinelConfig = this.getSentinelConfig(properties);
                    RedisClusterConfiguration clusterConfiguration = this.getClusterConfiguration(properties);
                    if (sentinelConfig != null) {
                        connectionBuilder.addConstructorArgValue(sentinelConfig);
                    } else if (clusterConfiguration != null) {
                        connectionBuilder.addConstructorArgValue(clusterConfiguration);
                    } else {
                        connectionBuilder.addConstructorArgValue(this.getStandaloneConfig(properties));
                    }

                    connectionBuilder.addConstructorArgValue(clientConfig);
                    String connectionBeanName = key + "RedisConnectionFactory";
                    listableBeanFactory.registerBeanDefinition(connectionBeanName, connectionBuilder.getBeanDefinition());
                    RedisConnectionFactory connectionFactory = (RedisConnectionFactory)beanFactory.getBean(connectionBeanName);
                    BeanDefinitionBuilder redisTemplateBuilder = BeanDefinitionBuilder.rootBeanDefinition(RedisTemplate.class);
                    this.addBeanDefinitionProperty(redisTemplateBuilder, "connectionFactory", connectionFactory);
                    String redisTemplateName = key + "RedisTemplate";
                    listableBeanFactory.registerBeanDefinition(redisTemplateName, redisTemplateBuilder.getBeanDefinition());
                    
                    // 配置RedisTemplate的序列化器
                    RedisTemplate<Object, Object> redisTemplate = (RedisTemplate<Object, Object>) beanFactory.getBean(redisTemplateName);
                    redisTemplate.setKeySerializer(new StringRedisSerializer());
                    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
                    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
                    redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
                    redisTemplate.afterPropertiesSet();
                    BeanDefinitionBuilder stringRedisTemplateBuilder = BeanDefinitionBuilder.rootBeanDefinition(StringRedisTemplate.class);
                    this.addBeanDefinitionProperty(stringRedisTemplateBuilder, "connectionFactory", connectionFactory);
                    String stringTemplate = key + "StringRedisTemplate";
                    listableBeanFactory.registerBeanDefinition(stringTemplate, stringRedisTemplateBuilder.getBeanDefinition());
                    StringRedisTemplate stringRedisTemplate = (StringRedisTemplate)beanFactory.getBean(stringTemplate);
                    BeanDefinitionBuilder callManageBuilder = BeanDefinitionBuilder.rootBeanDefinition(CallManage.class);
                    callManageBuilder.addConstructorArgValue(stringRedisTemplate);
                    String callManageName = key + "CallManage";
                    listableBeanFactory.registerBeanDefinition(callManageName, callManageBuilder.getBeanDefinition());
                    BeanDefinitionBuilder lockHandlerBuilder = BeanDefinitionBuilder.rootBeanDefinition(LockHandler.class);
                    lockHandlerBuilder.addConstructorArgValue(stringRedisTemplate);
                    String lockHandlerName = key + "LockHandler";
                    listableBeanFactory.registerBeanDefinition(lockHandlerName, lockHandlerBuilder.getBeanDefinition());
                    BeanDefinitionBuilder generatorBuilder = BeanDefinitionBuilder.rootBeanDefinition(Generator.class);
                    generatorBuilder.addConstructorArgValue(stringRedisTemplate);
                    String generatorName = key + "Generator";
                    listableBeanFactory.registerBeanDefinition(generatorName, generatorBuilder.getBeanDefinition());
                });
            }

            this.isSalveInit = true;
        }
    }

    private void addBeanDefinitionProperty(BeanDefinitionBuilder definitionBuilder, String propertyName, Object propertyValue) {
        if (!ObjectUtils.isEmpty(propertyValue)) {
            definitionBuilder.addPropertyValue(propertyName, propertyValue);
        }

    }
}
