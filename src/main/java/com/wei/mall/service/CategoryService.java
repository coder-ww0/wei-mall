package com.wei.mall.service;

import com.github.pagehelper.PageInfo;
import com.wei.mall.model.pojo.Category;
import com.wei.mall.model.request.AddCategoryReq;
import com.wei.mall.model.request.UpdateCategoryReq;
import com.wei.mall.model.vo.CategoryVo;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author wei
 * @date 2021/11/10 12:33
 * @description: 分类目录service
 */
public interface CategoryService {
    void add(AddCategoryReq addCategoryReq);

    void update(Category updateCategoryReq);

    void delete(Integer id);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVo> listCategoryForCustomer();
}
