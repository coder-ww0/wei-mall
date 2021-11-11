package com.wei.mall.controller;

import com.wei.mall.common.ApiRestResponse;
import com.wei.mall.filter.UserFilter;
import com.wei.mall.model.pojo.User;
import com.wei.mall.model.vo.CartVo;
import com.wei.mall.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wei
 * @date 2021/11/11 15:10
 * @description: 购物车Controller
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @ApiOperation("购物车列表")
    @GetMapping("/list")
    public ApiRestResponse list() {
        // 内部获取用户ID，防止横向越权
        List<CartVo> cartVoList = cartService.list(UserFilter.currentUser.getId());
        return ApiRestResponse.success(cartVoList);
    }

    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count) {
        List<CartVo> cartVoList = cartService.add(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVoList);
    }

    @ApiOperation("更新购物车")
    @PostMapping("/update")
    public ApiRestResponse update(@RequestParam Integer productId, @RequestParam Integer count) {
        List<CartVo> cartVoList = cartService.update(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVoList);
    }

    @ApiOperation("删除购物车")
    @PostMapping("/delete")
    public ApiRestResponse delete(@RequestParam Integer productId) {
        List<CartVo> cartVoList = cartService.delete(UserFilter.currentUser.getId(), productId);
        return ApiRestResponse.success(cartVoList);
    }

    @ApiOperation("选择/不选择购物车中的某商品")
    @PostMapping("/select")
    public ApiRestResponse select(@RequestParam Integer productId, @RequestParam Integer selected) {
        List<CartVo> cartVoList = cartService.selectOrNot(UserFilter.currentUser.getId(), productId, selected);
        return ApiRestResponse.success(cartVoList);
    }

    @ApiOperation("全选择/不选择购物车中的某商品")
    @PostMapping("/selectAll")
    public ApiRestResponse select(@RequestParam Integer selected) {
        List<CartVo> cartVoList = cartService.selectAllOrNot(UserFilter.currentUser.getId(), selected);
        return ApiRestResponse.success(cartVoList);
    }
}
