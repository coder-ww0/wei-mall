package com.wei.mall.service;

import com.github.pagehelper.PageInfo;
import com.wei.mall.model.request.CreateOrderReq;
import com.wei.mall.model.vo.OrderVo;

/**
 * @author wei
 * @date 2021/11/11 19:42
 * @description: TODO
 */
public interface OrderService {
    String create(CreateOrderReq createOrderReq);

    OrderVo detail(String orderNo);

    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo);

    void pay(String orderNo);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    void deliver(String orderNo);

    void finish(String orderNo);
}
