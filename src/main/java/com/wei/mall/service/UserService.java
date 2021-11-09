package com.wei.mall.service;

import com.wei.mall.exception.WeiMallException;
import com.wei.mall.model.pojo.User;

/**
 * @author wei
 * @date 2021/11/9 14:35
 * @description: TODO
 */
public interface UserService {
    public User getUser();

    void register(String userName, String password) throws WeiMallException;

    User login(String userName, String password) throws WeiMallException;

    void updateInformation(User user) throws WeiMallException;

    boolean checkAdminRole(User user);
}
