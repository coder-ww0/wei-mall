package com.wei.mall.exception;

import com.wei.mall.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wei
 * @date 2021/11/9 20:22
 * @description: 处理统一异常的handler
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e) {
        log.error("Default Exception: ", e);
        return ApiRestResponse.error(WeiMallExceptionEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(WeiMallException.class)
    @ResponseBody
    public Object handleWeiMallException(WeiMallException e) {
        log.error("WeiMalException: ", e);
        return ApiRestResponse.error(e.getCode(), e.getMessage());
    }
}
