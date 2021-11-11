package com.wei.mall.service.impl;

import com.wei.mall.common.Constant;
import com.wei.mall.controller.CartController;
import com.wei.mall.exception.WeiMallException;
import com.wei.mall.exception.WeiMallExceptionEnum;
import com.wei.mall.model.dao.CartMapper;
import com.wei.mall.model.dao.ProductMapper;
import com.wei.mall.model.pojo.Cart;
import com.wei.mall.model.pojo.Product;
import com.wei.mall.model.vo.CartVo;
import com.wei.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.awt.geom.AreaOp;

import java.util.List;

/**
 * @author wei
 * @date 2021/11/11 15:47
 * @description: 购物车的实现类
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;

    @Override
    public List<CartVo> add(Integer userId, Integer productId, Integer count) {
        // 判断商品是否有效
        validProduct(productId, count);

        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            // 这个商品之前不在购物车里，需要新增一个记录
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.insertSelective(cart);
        } else {
            // 这个商品已经在购物车里面，数量相加
            count = cart.getQuantity() + count;
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }

    private void validProduct(Integer productId, Integer count) {
        Product product = productMapper.selectByPrimaryKey(productId);
        // 判断商品是否存在，商品是否上架
        if (product == null && product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
            throw new WeiMallException(WeiMallExceptionEnum.NOT_SALE);
        }
        // 判断商品库存
        if (count > product.getStock()) {
            throw new WeiMallException(WeiMallExceptionEnum.NOT_ENOUGH);
        }
    }

    @Override
    public List<CartVo> list(Integer userId) {
        List<CartVo> cartVos = cartMapper.selectList(userId);
        for (int i = 0; i < cartVos.size(); i++) {
            CartVo cartVo = cartVos.get(i);
            cartVo.setTotalPrice(cartVo.getPrice() * cartVo.getQuantity());
        }
        return cartVos;
    }

    @Override
    public List<CartVo> update(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            // 如果商品此时不在购物车中，无法更新
            throw new WeiMallException(WeiMallExceptionEnum.UPDATE_FAILED);
        } else {
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVo> delete(Integer userId, Integer productId) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            // 如果商品此时不在购物车中，无法删除
            throw new WeiMallException(WeiMallExceptionEnum.DELETE_FAILED);
        } else {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.list(userId);
    }

    @Override
    public List<CartVo> selectOrNot(Integer userId, Integer productId, Integer selected) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new WeiMallException(WeiMallExceptionEnum.UPDATE_FAILED);
        } else {
            cartMapper.selectOrNot(userId, productId, selected);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVo> selectAllOrNot(Integer userId, Integer selected) {
        // 改变选中状态
        cartMapper.selectOrNot(userId, null, selected);
        return this.list(userId);
    }
}
