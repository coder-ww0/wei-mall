package com.wei.mall.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wei.mall.common.ApiRestResponse;
import com.wei.mall.common.Constant;
import com.wei.mall.exception.WeiMallException;
import com.wei.mall.exception.WeiMallExceptionEnum;
import com.wei.mall.model.dao.ProductMapper;
import com.wei.mall.model.pojo.Product;
import com.wei.mall.model.query.ProductListQuery;
import com.wei.mall.model.request.AddProductReq;
import com.wei.mall.model.request.ProductListReq;
import com.wei.mall.model.vo.CategoryVo;
import com.wei.mall.service.CategoryService;
import com.wei.mall.service.ProductService;
import io.swagger.models.auth.In;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.AbstractDataBufferDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.rmi.PortableRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wei
 * @date 2021/11/10 21:16
 * @description: 具体的实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void add(AddProductReq addProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);
        Product oldProduct = productMapper.selectByName(addProductReq.getName());
        if (oldProduct != null) {
            throw new WeiMallException(WeiMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new WeiMallException(WeiMallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void update(Product updateProduct) {
        Product productOld = productMapper.selectByName(updateProduct.getName());
        // 同名且不同id，不能继续修改
        if (productOld != null && !productOld.getId().equals(updateProduct.getId())) {
            throw new WeiMallException(WeiMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (count == 0) {
            throw new WeiMallException(WeiMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Product productOld = productMapper.selectByPrimaryKey(id);
        if (productOld == null) {
            throw new WeiMallException(WeiMallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new WeiMallException(WeiMallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        productMapper.batchUpdateSellStatus(ids, sellStatus);
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }

    @Override
    public Product detail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }

    @Override
    public PageInfo list(ProductListReq productListReq) {
        // 构建query对象
        ProductListQuery productListQuery = new ProductListQuery();

        // 搜索处理
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {
            String keyword = new StringBuilder().append("%").append(productListReq.getKeyword())
                    .append("%").toString();
            productListQuery.setKeyword(keyword);
        }
        // 目录处理，如果查某个目录下的商品，不仅是需要查出改目录下的，还要把所有子目录的所有商品都查出来，所以要拿到一个目录id的List
        if (productListReq.getCategoryId() != null) {
            List<CategoryVo> categoryVoList = categoryService.listCategoryForCustomer(productListReq.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVoList, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }
        // 排序处理
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }
        List<Product> productList = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }

    private void getCategoryIds(List<CategoryVo> categoryVoList, ArrayList<Integer> categoryIds) {
        for (int i = 0; i < categoryVoList.size(); i++) {
            CategoryVo categoryVo = categoryVoList.get(i);
            if (categoryVo != null) {
                categoryIds.add(categoryVo.getId());
                getCategoryIds(categoryVo.getChildCategory(), categoryIds);
            }
        }
    }
}
