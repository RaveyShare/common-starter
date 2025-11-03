//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ravey.common.redis.autoconfigure;

import com.ravey.common.redis.CallManage;
import com.ravey.common.redis.Generator;
import com.ravey.common.redis.LockHandler;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableConfigurationProperties({RedisProperties.class})
@AutoConfigureBefore({RedisAutoConfiguration.class})
public class MasterRedisConfiguration extends RedisConnectionConfiguration {
    public static final String MASTER_TEMPLATE = "redisTemplate";
    public static final String MASTER_STRING_TEMPLATE = "stringRedisTemplate";
    public static final String PREFIX = "master";
    private final RedisProperties redisProperties;

    public MasterRedisConfiguration(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean(
            destroyMethod = "shutdown"
    )
    @ConditionalOnMissingBean({ClientResources.class})
    public ClientResources lettuceClientResources() {
        return DefaultClientResources.create();
    }

    @Primary
    @Bean({"masterRedisConnectionFactory"})
    LettuceConnectionFactory masterRedisConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources) {
        LettuceClientConfiguration clientConfig = this.getLettuceClientConfiguration(builderCustomizers, clientResources, this.redisProperties);
        return this.createLettuceConnectionFactory(clientConfig, this.redisProperties);
    }

    @Primary
    @Bean({"redisTemplate"})
    public RedisTemplate<Object, Object> masterRedisTemplate(@Qualifier("masterRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        
        // 设置key的序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // 设置value的序列化器
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }

    @Primary
    @Bean({"stringRedisTemplate"})
    public StringRedisTemplate masterStringRedisTemplate(@Qualifier("masterRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Primary
    @Bean({"masterCallManage"})
    public CallManage redisCallManage(@Qualifier("stringRedisTemplate") StringRedisTemplate stringRedisTemplate) {
        return new CallManage(stringRedisTemplate);
    }

    @Primary
    @Bean({"masterLockHandler"})
    public LockHandler redisLockHandler(@Qualifier("stringRedisTemplate") StringRedisTemplate stringRedisTemplate) {
        return new LockHandler(stringRedisTemplate);
    }

    @Primary
    @Bean({"masterGenerator"})
    public Generator redisGenerator(@Qualifier("stringRedisTemplate") StringRedisTemplate stringRedisTemplate) {
        return new Generator(stringRedisTemplate);
    }
}
