//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ravey.common.redis.autoconfigure;

import io.lettuce.core.resource.ClientResources;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class RedisConnectionConfiguration {
    public static final String CONNECTION_FACTORY = "RedisConnectionFactory";
    public static final String LOCK_HANDLER = "LockHandler";
    public static final String CALL_MANAGE = "CallManage";
    public static final String GENERATOR = "Generator";

    public RedisConnectionConfiguration() {
    }

    protected final RedisStandaloneConfiguration getStandaloneConfig(RedisProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(properties.getUrl())) {
            ConnectionInfo connectionInfo = this.parseUrl(properties.getUrl());
            config.setHostName(connectionInfo.getHostName());
            config.setPort(connectionInfo.getPort());
            config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
        } else {
            config.setHostName(properties.getHost());
            config.setPort(properties.getPort());
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }

        config.setDatabase(properties.getDatabase());
        return config;
    }

    protected final RedisSentinelConfiguration getSentinelConfig(RedisProperties properties) {
        RedisProperties.Sentinel sentinelProperties = properties.getSentinel();
        if (sentinelProperties != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(sentinelProperties.getMaster());
            config.setSentinels(this.createSentinels(sentinelProperties));
            if (properties.getPassword() != null) {
                config.setPassword(RedisPassword.of(properties.getPassword()));
            }

            config.setDatabase(properties.getDatabase());
            return config;
        } else {
            return null;
        }
    }

    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList();
        Iterator var3 = sentinel.getNodes().iterator();

        while(var3.hasNext()) {
            String node = (String)var3.next();

            try {
                String[] parts = StringUtils.split(node, ":");

                assert parts != null;

                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            } catch (RuntimeException var6) {
                RuntimeException ex = var6;
                throw new IllegalStateException("Invalid redis sentinel property '" + node + "'", ex);
            }
        }

        return nodes;
    }

    protected final RedisClusterConfiguration getClusterConfiguration(RedisProperties properties) {
        if (properties.getCluster() == null) {
            return null;
        } else {
            RedisProperties.Cluster clusterProperties = properties.getCluster();
            RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
            if (clusterProperties.getMaxRedirects() != null) {
                config.setMaxRedirects(clusterProperties.getMaxRedirects());
            }

            if (properties.getPassword() != null) {
                config.setPassword(RedisPassword.of(properties.getPassword()));
            }

            return config;
        }
    }

    protected LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration, RedisProperties redisProperties) {
        RedisSentinelConfiguration sentinelConfig = this.getSentinelConfig(redisProperties);
        if (sentinelConfig != null) {
            return new LettuceConnectionFactory(sentinelConfig, clientConfiguration);
        } else {
            RedisClusterConfiguration clusterConfiguration = this.getClusterConfiguration(redisProperties);
            return clusterConfiguration != null ? new LettuceConnectionFactory(clusterConfiguration, clientConfiguration) : new LettuceConnectionFactory(this.getStandaloneConfig(redisProperties), clientConfiguration);
        }
    }

    protected LettuceClientConfiguration getLettuceClientConfiguration(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources, RedisProperties redisProperties) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = this.createBuilder(redisProperties.getLettuce().getPool());
        this.applyProperties(builder, redisProperties);
        if (StringUtils.hasText(redisProperties.getUrl())) {
            this.customizeConfigurationFromUrl(builder, redisProperties);
        }

        builder.clientResources(clientResources);
        builderCustomizers.orderedStream().forEach((customizer) -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    protected LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        return pool == null ? LettuceClientConfiguration.builder() : (new PoolBuilderFactory()).createBuilder(pool);
    }

    protected void applyProperties(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder, RedisProperties redisProperties) {
        if (redisProperties.getSsl().isEnabled()) {
            builder.useSsl();
        }

        if (redisProperties.getTimeout() != null) {
            builder.commandTimeout(redisProperties.getTimeout());
        }

        if (redisProperties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = redisProperties.getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(redisProperties.getLettuce().getShutdownTimeout());
            }
        }

        if (StringUtils.hasText(redisProperties.getClientName())) {
            builder.clientName(redisProperties.getClientName());
        }

    }

    protected void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder, RedisProperties redisProperties) {
        ConnectionInfo connectionInfo = this.parseUrl(redisProperties.getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }

    }

    protected ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            boolean useSsl = url.startsWith("rediss://");
            String password = null;
            if (uri.getUserInfo() != null) {
                password = uri.getUserInfo();
                int index = password.indexOf(58);
                if (index >= 0) {
                    password = password.substring(index + 1);
                }
            }

            return new ConnectionInfo(uri, useSsl, password);
        } catch (URISyntaxException var6) {
            URISyntaxException ex = var6;
            throw new IllegalArgumentException("Malformed url '" + url + "'", ex);
        }
    }

    static class ConnectionInfo {
        private final URI uri;
        private final boolean useSsl;
        private final String password;

        ConnectionInfo(URI uri, boolean useSsl, String password) {
            this.uri = uri;
            this.useSsl = useSsl;
            this.password = password;
        }

        boolean isUseSsl() {
            return this.useSsl;
        }

        String getHostName() {
            return this.uri.getHost();
        }

        int getPort() {
            return this.uri.getPort();
        }

        String getPassword() {
            return this.password;
        }
    }

    static class PoolBuilderFactory {
        PoolBuilderFactory() {
        }

        LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool properties) {
            return LettucePoolingClientConfiguration.builder().poolConfig(this.getPoolConfig(properties));
        }

        private GenericObjectPoolConfig<?> getPoolConfig(RedisProperties.Pool properties) {
            GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig();
            config.setMaxTotal(properties.getMaxActive());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMinIdle(properties.getMinIdle());
            if (properties.getTimeBetweenEvictionRuns() != null) {
                config.setTimeBetweenEvictionRuns(properties.getTimeBetweenEvictionRuns());
            }

            if (properties.getMaxWait() != null) {
                config.setMaxWait(properties.getMaxWait());
            }

            return config;
        }
    }
}
