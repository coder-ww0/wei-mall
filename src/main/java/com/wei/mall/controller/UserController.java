package com.wei.mall.controller;

import com.wei.mall.model.pojo.User;
import com.wei.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
