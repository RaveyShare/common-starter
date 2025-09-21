
package com.ravey.common.redis.execption;

public class CustomRedisException extends RuntimeException {
    private static final long serialVersionUID = 2400747019489479485L;
    private final String code;
    private final String message;

    public CustomRedisException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public CustomRedisException(RedisError error) {
        super(error.getErrMsg());
        this.code = error.getErrCode();
        this.message = error.getErrMsg();
    }

    public CustomRedisException(Throwable e) {
        super(e);
        this.code = "unknown";
        this.message = e.getMessage();
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
