//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ravey.common.redis.properties;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "spring.data"
)
public class CustomRedisGroupProperties {
    public static final String BC_REDIS_GROUP = "spring.data";
    private List<CustomRedisProperties> slaveRedisList = new ArrayList();

    public CustomRedisGroupProperties() {
    }

    public List<CustomRedisProperties> getSlaveRedisList() {
        return this.slaveRedisList;
    }

    public void setSlaveRedisList(List<CustomRedisProperties> slaveRedisList) {
        this.slaveRedisList = slaveRedisList;
    }
}
