package com.wei.mall.service.impl;

import com.wei.mall.exception.WeiMallException;
import com.wei.mall.exception.WeiMallExceptionEnum;
import com.wei.mall.model.dao.UserMapper;
import com.wei.mall.model.pojo.User;
import com.wei.mall.service.UserService;
import com.wei.mall.util.MD5Utils;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

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

    @Override
    public void register(String userName, String password) throws WeiMallException {
        // 查询用户名是否存在
        User result = userMapper.selectByName(userName);
        if (result != null) {
            throw new WeiMallException(WeiMallExceptionEnum.NAME_EXISTED);
        }
        // 用户不存在,将用户写入到数据库
        User user = new User();
        user.setUsername(userName);
//        user.setPassword(password);
        try {
            user.setPassword(MD5Utils.getMD5Str(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            throw new WeiMallException(WeiMallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String userName, String password) throws WeiMallException {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        User user = userMapper.selectLogin(userName, md5Password);
        if (user == null) {
            throw new WeiMallException(WeiMallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    @Override
    public void updateInformation(User user) throws WeiMallException {
        // 更新个性签名
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 1) {
            throw new WeiMallException(WeiMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkAdminRole(User user) {
        // 1是普通用户，2是管理员
        return user.getRole().equals(2);
    }
}
