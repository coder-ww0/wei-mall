package com.wei.mall.service;

import com.github.pagehelper.PageInfo;
import com.wei.mall.model.pojo.Product;
import com.wei.mall.model.request.AddProductReq;
import com.wei.mall.model.request.ProductListReq;

/**
 * @author wei
 * @date 2021/11/10 21:15
 * @description: 商品Service
 */
public interface ProductService {

    void add(AddProductReq addProductReq);

    void update(Product updateProduct);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductListReq productListReq);
}
