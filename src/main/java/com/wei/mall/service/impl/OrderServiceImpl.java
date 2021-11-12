package com.wei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mysql.cj.PreparedQuery;
import com.wei.mall.common.Constant;
import com.wei.mall.exception.WeiMallException;
import com.wei.mall.exception.WeiMallExceptionEnum;
import com.wei.mall.filter.UserFilter;
import com.wei.mall.model.dao.CartMapper;
import com.wei.mall.model.dao.OrderItemMapper;
import com.wei.mall.model.dao.OrderMapper;
import com.wei.mall.model.dao.ProductMapper;
import com.wei.mall.model.pojo.Cart;
import com.wei.mall.model.pojo.Order;
import com.wei.mall.model.pojo.OrderItem;
import com.wei.mall.model.pojo.Product;
import com.wei.mall.model.request.CreateOrderReq;
import com.wei.mall.model.vo.CartVo;
import com.wei.mall.model.vo.OrderItemVo;
import com.wei.mall.model.vo.OrderVo;
import com.wei.mall.service.CartService;
import com.wei.mall.service.OrderService;
import com.wei.mall.service.UserService;
import com.wei.mall.util.OrderCodeFactory;
import com.wei.mall.util.QRCodeGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wei
 * @date 2021/11/11 19:42
 * @description: 订单service实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Value("${file.upload.ip}")
    private String ip;

    @Autowired
    private UserService userService;

    /**
     *     遇到异常会回滚
      */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderReq createOrderReq) {
        // 拿到用户ID
        Integer userId = UserFilter.currentUser.getId();
        // 从购物车中查找已经勾选的商品
        List<CartVo> cartVoList = cartService.list(userId);
        List<CartVo> cartVoListTemp = new ArrayList<>();
        for (int i = 0; i < cartVoList.size(); i++) {
            CartVo cartVo = cartVoList.get(i);
            if (cartVo.getSelected().equals(Constant.Cart.CHECKED)) {
                cartVoListTemp.add(cartVo);
            }
        }
        cartVoList = cartVoListTemp;
        // 如果购物车已勾选的为空，报错
        if (CollectionUtils.isEmpty(cartVoList)) {
            throw new WeiMallException(WeiMallExceptionEnum.CART_EMPTY);
        }
        // 判断商品是否存在，上下架状态，库存
        validSaleStatusAndStock(cartVoList);
        // 把购物车对象转换为订单item对象
        List<OrderItem> orderItemList = cartVoListToOrderItemList(cartVoList);
        // 扣库存
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new WeiMallException(WeiMallExceptionEnum.NOT_ENOUGH);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        // 把购物车中的已勾选的商品删除
        cleanCart(cartVoList);
        // 生成订单
        Order order = new Order();
        // 生成订单号，有独立的规则
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        // 插入到order表
        orderMapper.insertSelective(order);
        // 循环保存每个商品到order_item表
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }
        // 把结果返回
        return orderNo;

    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVo> cartVoList) {
        for (int i = 0; i < cartVoList.size(); i++) {
            CartVo cartVo = cartVoList.get(i);
            cartMapper.deleteByPrimaryKey(cartVo.getId());
        }
    }

    private List<OrderItem> cartVoListToOrderItemList(List<CartVo> cartVoList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < cartVoList.size(); i++) {
            CartVo cartVo = cartVoList.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVo.getProductId());
            // 记录商品的快照信息
            orderItem.setProductName(cartVo.getProductName());
            orderItem.setProductImg(cartVo.getProductImage());
            orderItem.setUnitPrice(cartVo.getPrice());
            orderItem.setQuantity(cartVo.getQuantity());
            orderItem.setTotalPrice(cartVo.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void validSaleStatusAndStock(List<CartVo> cartVoList) {
        for (int i = 0; i < cartVoList.size(); i++) {
            CartVo cartVo = cartVoList.get(i);
            Product product = productMapper.selectByPrimaryKey(cartVo.getProductId());
            // 判断商品是否存在，商品是否上架
            if (product == null && product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new WeiMallException(WeiMallExceptionEnum.NOT_SALE);
            }
            // 判断商品库存
            if (cartVo.getQuantity() > product.getStock()) {
                throw new WeiMallException(WeiMallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    @Override
    public OrderVo detail(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 订单不存在，则报错
        if (order == null) {
            throw new WeiMallException(WeiMallExceptionEnum.NO_ORDER);
        }
        // 订单存在，需要判断所属
        Integer id = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(id)) {
            throw new WeiMallException(WeiMallExceptionEnum.NOT_YOUR_ORDER);
        }
        OrderVo orderVo = getOrderVo(order);
        return orderVo;
    }

    private OrderVo getOrderVo(Order order) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order, orderVo);
        // 获取订单对应的orderItemVoList
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVo> orderItemVoList = new ArrayList<>();
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(orderItem, orderItemVo);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVo.getOrderStatus()).getValue());
        return orderVo;
    }

    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize) {
        Integer userId = UserFilter.currentUser.getId();
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        List<OrderVo> orderVoList = orderListToOrderVoList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVoList);
        return pageInfo;
    }

    private List<OrderVo> orderListToOrderVoList(List<Order> orderList) {
        List<OrderVo> orderVoList = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderVo orderVo = getOrderVo(order);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    @Override
    public void cancel(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 订单不存在，报错
        if (order == null) {
            throw new WeiMallException(WeiMallExceptionEnum.NO_ORDER);
        }
        // 订单存在，需要判断所属
        Integer id = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(id)) {
            throw new WeiMallException(WeiMallExceptionEnum.NOT_YOUR_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new WeiMallException(WeiMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public String qrcode(String orderNo) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 获取局域网的ip地址
//        try {
//            ip = InetAddress.getLocalHost().getHostAddress();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
        String address = ip + ":" + request.getLocalPort();
        String payUrl = "http://" + address + "/pay?orderNo=" + orderNo;
        try {
            QRCodeGenerator.generatorQRCodeImage(payUrl, 350, 350, Constant.FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }
        String pngAddress = "http://" + address + "/images/" + orderNo + ".png";
        return pngAddress;
    }

    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 订单不存在，则报错
        if (order == null) {
            throw new WeiMallException(WeiMallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus() == Constant.OrderStatusEnum.NOT_PAID.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new WeiMallException(WeiMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllForAdmin() ;
        List<OrderVo> orderVoList = orderListToOrderVoList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVoList);
        return pageInfo;
    }

    @Override
    public void deliver(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 订单不存在，则报错
        if (order == null) {
            throw new WeiMallException(WeiMallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus() == Constant.OrderStatusEnum.PAID.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new WeiMallException(WeiMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void finish(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        // 订单不存在，则报错
        if (order == null) {
            throw new WeiMallException(WeiMallExceptionEnum.NO_ORDER);
        }
        // 如果是普通用户，就要校验订单的所属
        if (!userService.checkAdminRole(UserFilter.currentUser) && !order.getUserId().equals(UserFilter.currentUser.getId())) {
            throw new WeiMallException(WeiMallExceptionEnum.NOT_YOUR_ORDER);
        }
        // 发货后可以完结订单
        if (order.getOrderStatus() == Constant.OrderStatusEnum.DELIVERED.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new WeiMallException(WeiMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }
}
