package com.wei.mall.exception;

/**
 * @author wei
 * @date 2021/11/9 18:14
 * @description: TODO
 */
public enum WeiMallExceptionEnum {
    /**
     * 用户名不能为空
     */
    NEED_USER_NAME(1001, "用户名不能为空"),
    NEED_PASSWORD(1002, "密码不能为空"),
    PASSWORD_TOO_SHORT(1003, "密码长度不能小于8位"),
    NAME_EXISTED(10004, "不允许重名，注册失败"),
    INSERT_FAILED(10005, "插入失败，请重试"),
    WRONG_PASSWORD(10006, "密码错误"),
    NEED_LOGIN(1007, "用户未登录"),
    UPDATE_FAILED(10008, "更新失败"),
    NEED_ADMIN(10009, "无管理员权限"),

    SYSTEM_ERROR(20000, "系统异常");

    /**
     * 异常码
     */
    Integer code;
    /**
     * 异常信息
     */
    String msg;

    WeiMallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}