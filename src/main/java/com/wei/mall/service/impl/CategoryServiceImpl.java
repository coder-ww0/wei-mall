package com.wei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wei.mall.exception.WeiMallException;
import com.wei.mall.exception.WeiMallExceptionEnum;
import com.wei.mall.model.dao.CategoryMapper;
import com.wei.mall.model.pojo.Category;
import com.wei.mall.model.request.AddCategoryReq;
import com.wei.mall.model.vo.CategoryVo;
import com.wei.mall.service.CategoryService;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.sql.rowset.CachedRowSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wei
 * @date 2021/11/10 12:34
 * @description: TODO
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加目录的方法
     */
    @Override
    public void add(AddCategoryReq addCategoryReq) {
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReq, category);
        Category oldCategory = categoryMapper.selectByName(addCategoryReq.getName());
        if (oldCategory != null) {
            throw new WeiMallException(WeiMallExceptionEnum.NAME_EXISTED);
        }
        int count = categoryMapper.insertSelective(category);
        if (count == 0) {
            throw new WeiMallException(WeiMallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void update(Category updateCategory) {
        if (updateCategory.getName() != null) {
            Category categoryOld = categoryMapper.selectByName(updateCategory.getName());
            if (categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())) {
                throw new WeiMallException(WeiMallExceptionEnum.NAME_EXISTED);
            }
        }
        int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            throw new WeiMallException((WeiMallExceptionEnum.UPDATE_FAILED));
        }
    }

    @Override
    public void delete(Integer id) {
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        // 查不到记录，无法删除，删除失败
        if (categoryOld == null) {
            throw new WeiMallException(WeiMallExceptionEnum.DELETE_FAILED);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new WeiMallException(WeiMallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type, order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo(categoryList);
        return pageInfo;
    }

    @Override
    @Cacheable(value = "listCategoryForCustomer")
    public List<CategoryVo> listCategoryForCustomer(Integer parentId) {
        ArrayList<CategoryVo> categoryVoList = new ArrayList<>();
        recursivelyFindCategories(categoryVoList,  parentId);
        return categoryVoList;
    }

    private void recursivelyFindCategories(List<CategoryVo> categoryVoList, Integer parentId) {
        // 递归获取所有子类别，并组合成为一个"目录树"
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (int i = 0; i < categoryList.size(); i++) {
                Category category = categoryList.get(i);
                CategoryVo categoryVo = new CategoryVo();
                BeanUtils.copyProperties(category, categoryVo);
                categoryVoList.add(categoryVo);
                recursivelyFindCategories(categoryVo.getChildCategory(), categoryVo.getId());
            }
        }
    }
}
