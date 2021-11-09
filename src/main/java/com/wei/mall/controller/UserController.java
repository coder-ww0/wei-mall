package com.wei.mall.controller;

import com.wei.mall.common.ApiRestResponse;
import com.wei.mall.common.Constant;
import com.wei.mall.exception.WeiMallException;
import com.wei.mall.exception.WeiMallExceptionEnum;
import com.wei.mall.model.pojo.User;
import com.wei.mall.service.UserService;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.plugin2.os.windows.SECURITY_ATTRIBUTES;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.RecursiveTask;

/**
 * @author wei
 * @date 2021/11/9 14:05
 * @description: 用户控制器
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @GetMapping("/test")
    public User personalPage() {
        return userService.getUser();
    }

    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("userName") String userName, @RequestParam("password") String password) throws WeiMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(WeiMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(WeiMallExceptionEnum.NEED_PASSWORD);
        }
        // 密码长度不能少于8位
        if (password.length() < 8) {
            return ApiRestResponse.error(WeiMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(userName, password);
        return ApiRestResponse.success();
    }

    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws WeiMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(WeiMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(WeiMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        // 保存用户信息时，不保存密码(返回给用户的信息中不存储密码)
        user.setPassword(null);
        session.setAttribute(Constant.WEI_MALL_USER, user);
        return ApiRestResponse.success(user);
    }

    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session, @RequestParam String signature) throws WeiMallException {
        User currentUser = (User)session.getAttribute(Constant.WEI_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(WeiMallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session) {
        session.removeAttribute(Constant.WEI_MALL_USER);
        return ApiRestResponse.success();
    }


    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws WeiMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(WeiMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(WeiMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        // 校验是否是管理员
        if (userService.checkAdminRole(user)) {
            // 是管理员执行对应的操作
            // 保存用户信息时，不保存密码(返回给用户的信息中不存储密码)
            user.setPassword(null);
            session.setAttribute(Constant.WEI_MALL_USER, user);
            return ApiRestResponse.success(user);
        } else {
            return ApiRestResponse.error(WeiMallExceptionEnum.NEED_ADMIN);
        }

    }
}
