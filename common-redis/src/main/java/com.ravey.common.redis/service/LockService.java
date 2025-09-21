//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ravey.common.redis.service;

import com.ravey.common.redis.LockHandler;
import com.ravey.common.redis.LockParam;
import com.ravey.common.redis.autoconfigure.CustomLockConfiguration;
import com.ravey.common.redis.execption.CustomRedisException;
import com.ravey.common.redis.execption.RedisError;

public abstract class LockService<T, R> {
    private final LockHandler lockHandler;
    private final LockParam lockParam;
    private long timeout;
    private long tryInterval;
    private long lockExpireTime;

    public LockService(LockParam lockParam) {
        this(getMasterLockHandler(), lockParam);
    }

    public LockService(LockHandler lockHandler, LockParam lockParam) {
        this.timeout = 3000L;
        this.tryInterval = 300L;
        this.lockExpireTime = 30000L;
        this.lockHandler = lockHandler;
        this.lockParam = lockParam;
    }

    private static LockHandler getMasterLockHandler() {
        return (LockHandler)CustomLockConfiguration.getBean("masterLockHandler");
    }

    public LockService<T, R> timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public LockService<T, R> interval(long tryInterval) {
        this.tryInterval = tryInterval;
        return this;
    }

    public LockService<T, R> expire(long lockExpireTime) {
        this.lockExpireTime = lockExpireTime;
        return this;
    }

    protected R lockFailed(T t) {
        throw new CustomRedisException(RedisError.LOCK_FAILED);
    }

    public abstract R doExecute(T t);

    public R execute(T t) {
        boolean locked = this.lockHandler.tryLock(this.lockParam, this.timeout, this.tryInterval, this.lockExpireTime);
        if (!locked) {
            return this.lockFailed(t);
        } else {
            try {
                return this.doExecute(t);
            } finally {
                this.lockHandler.releaseLock(this.lockParam);
            }
        }
    }


    public static void main(String[] args) {
        Boolean ret = (Boolean)(new LockService<Long, Boolean>(new LockParam("aaa")) {
            public Boolean doExecute(Long t) {
                return t.equals(1L);
            }

            public Boolean lockFailed(Long t) {
                System.out.println("lock failed");
                return false;
            }
        }).timeout(1000L).interval(500L).expire(10000L).execute(1L);
        System.out.println(ret);
    }
}
