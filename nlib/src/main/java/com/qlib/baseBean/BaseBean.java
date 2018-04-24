package com.qlib.baseBean;


import com.qjsonlib.annotation.JsonName;

public class BaseBean {
    @JsonName("message")
    private String message;
    @JsonName("code")
    private int code;
    @JsonName("success")
    private boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
