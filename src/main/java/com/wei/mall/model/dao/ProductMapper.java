package com.wei.mall.model.dao;

import com.wei.mall.model.pojo.Product;
import com.wei.mall.model.query.ProductListQuery;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    Product selectByName(String name);

    int batchUpdateSellStatus(@Param("ids") Integer[] ids, @Param("sellStatus")Integer sellStatus);

    List<Product> selectListForAdmin();

    List<Product> selectList(@Param("query") ProductListQuery query);
}