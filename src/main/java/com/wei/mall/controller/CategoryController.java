package com.wei.mall.controller;

import com.github.pagehelper.PageInfo;
import com.wei.mall.common.ApiRestResponse;
import com.wei.mall.common.Constant;
import com.wei.mall.exception.WeiMallExceptionEnum;
import com.wei.mall.model.pojo.Category;
import com.wei.mall.model.pojo.User;
import com.wei.mall.model.request.AddCategoryReq;
import com.wei.mall.model.request.UpdateCategoryReq;
import com.wei.mall.model.vo.CategoryVo;
import com.wei.mall.service.CategoryService;
import com.wei.mall.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * @author wei
 * @date 2021/11/10 12:22
 * @description: TODO
 */
@Controller
public class CategoryController {

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 后台添加目录
     * @param session
     * @param addCategoryReq
     * @return
     */
    @ApiOperation("后台添加目录")
    @PostMapping("/admin/category/add")
    @ResponseBody
    public ApiRestResponse addCategory(HttpSession session, @Valid @RequestBody AddCategoryReq addCategoryReq) {
        // 需要判断用户是否登录以及对应的权限是否为管理员权限才能执行添加操作。因此需要引入session
        User currentUser = (User) session.getAttribute(Constant.WEI_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(WeiMallExceptionEnum.NEED_LOGIN);
        }
        // 校验是否是管理员
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            // 是管理员的情况下进行对应的操作。首先会去数据库中查询是否存在名字相同的分类。如果没有的话则进行对应的插入
            categoryService.add(addCategoryReq);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(WeiMallExceptionEnum.NEED_ADMIN);
        }
    }

    @ApiOperation("后台更新目录")
    @PostMapping("/admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(@Valid @RequestBody UpdateCategoryReq updateCategoryReq, HttpSession session) {
        // 需要判断用户是否登录以及对应的权限是否为管理员权限才能执行添加操作。因此需要引入session
        User currentUser = (User) session.getAttribute(Constant.WEI_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(WeiMallExceptionEnum.NEED_LOGIN);
        }
        // 校验是否是管理员
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            // 是管理员的情况下进行对应的操作。首先会去数据库中查询是否存在名字相同的分类。如果没有的话则进行对应的插入
            Category category = new Category();
            BeanUtils.copyProperties(updateCategoryReq, category);
            categoryService.update(category);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(WeiMallExceptionEnum.NEED_ADMIN);
        }
    }

    @ApiOperation("后台删除目录")
    @PostMapping("/admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleteCategory(@RequestParam Integer id) {
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台目录列表")
    @PostMapping("/admin/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台目录列表")
    @PostMapping("/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer() {
        List<CategoryVo> categoryVos = categoryService.listCategoryForCustomer();
        return ApiRestResponse.success(categoryVos);
    }
}
