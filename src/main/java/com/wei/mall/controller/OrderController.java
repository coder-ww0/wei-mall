package com.wei.mall.controller;

import com.github.pagehelper.PageInfo;
import com.wei.mall.common.ApiRestResponse;
import com.wei.mall.model.request.CreateOrderReq;
import com.wei.mall.model.vo.OrderVo;
import com.wei.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wei
 * @date 2021/11/11 19:38
 * @description: 订单Controller
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping("/create")
    public ApiRestResponse create(@RequestBody CreateOrderReq createOrderReq) {
        String orderNo = orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    @ApiOperation("前台订单模块")
    @GetMapping("/detail")
    public ApiRestResponse detail(@RequestParam String orderNo) {
        OrderVo orderVo = orderService.detail(orderNo);
        return ApiRestResponse.success(orderVo);
    }

    @ApiOperation("前台订单列表")
    @GetMapping("/list")
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.listForCustomer(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台订单取消")
    @PostMapping("/cancel")
    public ApiRestResponse cancel(@RequestParam String orderNo) {
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }

    @ApiOperation("生成支付二维码")
    @PostMapping("/qrcode")
    public ApiRestResponse qrcode(@RequestParam String orderNo) {
        String pngAddress = orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAddress);
    }

    @ApiOperation("支付接口")
    @PostMapping("/pay")
    public ApiRestResponse pay(@RequestParam String orderNo) {
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }
}
