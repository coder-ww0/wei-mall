package com.wei.mall.util;

import com.wei.mall.common.ApiRestResponse;
import com.wei.mall.common.Constant;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author wei
 * @date 2021/11/9 20:50
 * @description: 对密码进行加密
 */
public class MD5Utils {

    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest((strValue + Constant.SALT).getBytes()));
    }

    public static void main(String[] args) {
        String md5 = null;
        try {
            md5 = getMD5Str("12345678");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println(md5);
    }



}
