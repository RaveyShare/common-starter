package com.ravey.common.api.model;


import com.ravey.common.api.exception.ErrorCode;
import com.ravey.common.api.utils.MessageFormatter;

public final class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 8786892311981849335L;
    private final int code;
    private final String message;

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ServiceException(ErrorCode errorCode, Object... params) {
        super(params != null && params.length > 0 ? MessageFormatter.format(errorCode.getMessage(), params) : errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = params != null && params.length > 0 ? MessageFormatter.format(errorCode.getMessage(), params) : errorCode.getMessage();
    }

    public ServiceException(Throwable e, ErrorCode errorCode, Object... params) {
        super(e);
        this.code = errorCode.getCode();
        this.message = params != null && params.length > 0 ? MessageFormatter.format(errorCode.getMessage(), params) : errorCode.getMessage();
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
