package com.wei.mall.exception;

import com.wei.mall.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiRestResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: ", e);
        return handlerBindingResult(e.getBindingResult());
    }

    private ApiRestResponse handlerBindingResult(BindingResult result) {
        // 把异常处理做为对外的暴露的提示
        List<String> list = new ArrayList<>();
        if (result.hasErrors()) {
            List<ObjectError> allErrors = result.getAllErrors();
            for (ObjectError objectError : allErrors) {
                String message = objectError.getDefaultMessage();
                list.add(message);
            }
        }
        if (list.isEmpty()) {
            return ApiRestResponse.error(WeiMallExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiRestResponse.error(WeiMallExceptionEnum.REQUEST_PARAM_ERROR.getCode(), list.toString());
    }
}
