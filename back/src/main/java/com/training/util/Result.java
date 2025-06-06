package com.training.util;

import java.io.Serializable;

/**
 * 统一返回结果
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 成功 */
    public static final int SUCCESS = 200;
    /** 失败 */
    public static final int ERROR = 500;

    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return success("操作成功");
    }

    public static <T> Result<T> success(String msg) {
        return success(msg, null);
    }

    public static <T> Result<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<T>(SUCCESS, msg, data);
    }

    public static <T> Result<T> error() {
        return error("操作失败");
    }

    public static <T> Result<T> error(String msg) {
        return error(ERROR, msg);
    }

    public static <T> Result<T> error(int code, String msg) {
        return new Result<T>(code, msg, null);
    }

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
} 