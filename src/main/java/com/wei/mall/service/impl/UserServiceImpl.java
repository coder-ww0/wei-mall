package com.wei.mall.service.impl;

import com.wei.mall.model.dao.UserMapper;
import com.wei.mall.model.pojo.User;
import com.wei.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wei
 * @date 2021/11/9 14:36
 * @description: UserService实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }
}
