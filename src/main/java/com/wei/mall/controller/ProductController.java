package com.wei.mall.controller;

import com.github.pagehelper.PageInfo;
import com.wei.mall.common.ApiRestResponse;
import com.wei.mall.model.pojo.Product;
import com.wei.mall.model.request.ProductListReq;
import com.wei.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wei
 * @date 2021/11/11 12:25
 * @description: TODO
 */
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("商品详情")
    @GetMapping("/product/detail")
    public ApiRestResponse detail(@RequestParam Integer id) {
        Product product = productService.detail(id);
        return ApiRestResponse.success(product);
    }

    @ApiOperation("前台商品展示")
    @GetMapping("/product/list")
    public ApiRestResponse list(ProductListReq productListReq) {
        PageInfo list = productService.list(productListReq);
        return ApiRestResponse.success(list);
    }
}
