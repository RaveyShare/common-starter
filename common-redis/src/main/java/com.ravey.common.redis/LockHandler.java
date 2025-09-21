//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ravey.common.redis;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

public final class LockHandler {
    public static final long DEFAULT_LOCK_EXPIRE = 30000L;
    public static final long DEFAULT_LOCK_TRY_INTERVAL = 300L;
    public static final long DEFAULT_LOCK_TRY_TIMEOUT = 3000L;
    private final StringRedisTemplate redis;
    private final Logger logger;

    public LockHandler(StringRedisTemplate redis) {
        this.redis = redis;
        this.logger = LoggerFactory.getLogger(LockHandler.class);
    }

    public boolean getLock(LockParam lockParam, long timeout, long tryInterval, long lockExpireTime) {
        String key = "Lock:" + lockParam.getKey();

        try {
            if (StringUtils.hasText(key) && StringUtils.hasText(lockParam.getValue())) {
                long startTime = System.currentTimeMillis();

                while(true) {
                    Boolean isSuccess = this.redis.opsForValue().setIfAbsent(key, lockParam.getValue(), lockExpireTime, TimeUnit.MILLISECONDS);
                    if (isSuccess != null && isSuccess) {
                        return true;
                    }

                    if (System.currentTimeMillis() - startTime > timeout) {
                        return false;
                    }

                    Thread.sleep(tryInterval);
                }
            } else {
                return false;
            }
        } catch (Exception var12) {
            Exception e = var12;
            this.logger.error("获取redis分布式锁发生异常:[{}]", e.getMessage());
            return false;
        }
    }

    public boolean tryNeverExpireLock(LockParam lockParam) {
        return this.getLock(lockParam, -1L, 100L, -1L);
    }

    public boolean tryLock(LockParam lockParam) {
        return this.getLock(lockParam, 3000L, 300L, 30000L);
    }

    public boolean tryLock(LockParam lockParam, long timeout) {
        return this.getLock(lockParam, timeout, 300L, 30000L);
    }

    public boolean tryLock(LockParam lockParam, long timeout, long tryInterval) {
        return this.getLock(lockParam, timeout, tryInterval, 30000L);
    }

    public boolean tryLock(LockParam lockParam, long timeout, long tryInterval, long lockExpireTime) {
        return this.getLock(lockParam, timeout, tryInterval, lockExpireTime);
    }

    public void releaseLock(LockParam lockParam) {
        String key = "Lock:" + lockParam.getKey();
        String val = (String)this.redis.opsForValue().get(key);
        if (StringUtils.hasText(val) && lockParam.getValue().equals(val)) {
            this.redis.delete(key);
        }

    }
}
