

package com.ravey.common.redis.execption;

public enum RedisError {
    LOCK_FAILED("lock_failed", "获取锁失败，当前业务正在处理中！");

    private final String errCode;
    private final String errMsg;

    public String getErrCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    private RedisError(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}
