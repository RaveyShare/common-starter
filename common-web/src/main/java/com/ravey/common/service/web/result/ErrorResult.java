package com.ravey.common.service.web.result;


public class ErrorResult {
    private int code;
    private String msg;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ErrorResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
