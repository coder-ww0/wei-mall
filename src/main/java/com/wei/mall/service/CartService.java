package com.wei.mall.service;

import com.wei.mall.model.vo.CartVo;

import java.util.List;

/**
 * @author wei
 * @date 2021/11/11 15:47
 * @description: TODO
 */
public interface CartService {
    List<CartVo> add(Integer userId, Integer productId, Integer count);

    List<CartVo> list(Integer userId);

    List<CartVo> update(Integer userId, Integer productId, Integer count);

    List<CartVo> delete(Integer userId, Integer productId);

    List<CartVo> selectOrNot(Integer userId, Integer productId, Integer selected);

    List<CartVo> selectAllOrNot(Integer userId, Integer selected);
}
