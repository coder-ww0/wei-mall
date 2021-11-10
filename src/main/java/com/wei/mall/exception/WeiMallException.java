package com.wei.mall.exception;

import java.util.concurrent.Executors;

/**
 * @author wei
 * @date 2021/11/9 18:38
 * @description: 同一异常，用来抛出异常
 */
public class WeiMallException extends RuntimeException {
    private final Integer code;
    private final String message;

    public WeiMallException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public WeiMallException(WeiMallExceptionEnum exceptionEnum) {
        this(exceptionEnum.getCode(), exceptionEnum.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
