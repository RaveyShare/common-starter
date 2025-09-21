//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ravey.common.redis;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

public class CallManage {
    private final StringRedisTemplate redis;

    public CallManage(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public long getSurplus(String key, long seconds) {
        if (StringUtils.hasText(key) && seconds > 0L) {
            key = "Limit:" + key;
            String val = (String)this.redis.opsForValue().get(key);
            if (!StringUtils.hasText(val)) {
                this.redis.opsForValue().set(key, String.valueOf(System.currentTimeMillis()), seconds, TimeUnit.SECONDS);
                return 0L;
            } else {
                long byPast = System.currentTimeMillis() - Long.parseLong(val);
                if (byPast > 1000L) {
                    return seconds - byPast / 1000L;
                } else {
                    this.redis.opsForValue().set(key, String.valueOf(System.currentTimeMillis()), seconds, TimeUnit.SECONDS);
                    return seconds;
                }
            }
        } else {
            return 0L;
        }
    }

    public boolean isLimited(String key, long seconds, int max) {
        if (StringUtils.hasText(key) && seconds > 0L) {
            key = "Limit:" + key;
            Boolean isSuccess = this.redis.opsForValue().setIfAbsent(key, "1", seconds, TimeUnit.SECONDS);
            if (isSuccess != null && isSuccess) {
                return false;
            } else {
                Long times = this.redis.opsForValue().increment(key, 1L);
                if (times == null) {
                    throw new RuntimeException("redis限流自增返回为空");
                } else {
                    return times > (long)max;
                }
            }
        } else {
            return false;
        }
    }
}
