package com.wei.mall.common;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author wei
 * @date 2021/11/9 21:01
 * @description: 常量值
 */
@Component
public class Constant {
    /**
     * 盐值
     */
    public static final String SALT = "djsfakhfjksfnjKLN";

    public static final String WEI_MALL_USER = "wei_mall_user";


    public static String FILE_UPLOAD_DIR;
    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price acs");
    }

    public interface SaleStatus {
        // 商品下架
        int NOT_SALE = 0;
        // 商品上架状态
        int SALE = 1;
    }

    public interface Cart {
        // 购物车未选中
        int UN_CHECKED = 0;
        // 购物车选中
        int CHECKED = 1;
    }
}
