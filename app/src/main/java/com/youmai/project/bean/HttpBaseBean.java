package com.youmai.project.bean;

/**
 * 网络请求状态实体类
 */

public class HttpBaseBean {
    private int code;
    private String msg;
    public HttpBaseBean(){}

    public HttpBaseBean(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSussess() {
        if (code == 200) {
            return true;
        }
        return false;
    }

}
