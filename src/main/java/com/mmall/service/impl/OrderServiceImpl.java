package com.mmall.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Constant;
import com.mmall.common.ResponseService;
import com.mmall.dao.CartMapper;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.PayInfoMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.pojo.PayInfo;
import com.mmall.pojo.Product;
import com.mmall.service.IOrderService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PriceUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderVo;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService{
	
	private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	OrderMapper orderMapper;
	
	@Autowired
	OrderItemMapper orderItemMapper;

	@Autowired
	PayInfoMapper payInfoMapper;
	
	@Autowired
	CartMapper	cartMapper;
	
	@Autowired
	ProductMapper productMapper;
		
	@Override
	public ResponseService<Map<String, String>> pay(Integer userId, Long orderNo, String path) {
		Order order = orderMapper.selectByOrderNoAndUserId(userId, orderNo);
		if(order == null) {
			return ResponseService.createErrorResponseMessage("订单不存在");
		}
		if(order.getStatus() >= Constant.PayStatusEnum.PAIED.getCode()) {
			return ResponseService.createErrorResponseMessage("订单已付款");
		}
		try {
			Map<String, String> map = alipay(order, path);
			return ResponseService.createSuccessResponse(map);
		} catch (Exception e) {
			return ResponseService.createErrorResponseMessage(e.getMessage());
		}
	}

	private Map<String, String> alipay(Order order, String path) throws Exception {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuffer().append("ipeak 商城  扫码支付,订单号:").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("购买商品共").append(order.getPayment()).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        
        
//        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
//        // 创建好一个商品后添加至商品明细列表
//        goodsDetailList.add(goods1);
        List<OrderItem> orderItemList = orderItemMapper.getOrderItemByOrderNoAndUserId(order.getOrderNo(), order.getUserId());
        for(OrderItem item : orderItemList) {
        	GoodsDetail goods = GoodsDetail.newInstance(item.getProductId().toString(), item.getProductName(), 
        			PriceUtil.mul(item.getCurrentUnitPrice().doubleValue(), new Double(100)).intValue(), item.getQuantity());
        	goodsDetailList.add(goods);
        }

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
//        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
//        goodsDetailList.add(goods2);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
            .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
                            .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setGoodsDetailList(goodsDetailList);
        
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        AlipayTradeService tradeService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();
        
		AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
            	logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                // 需要修改为运行机器上的路径
                String fileName = String.format("qr-%s.png", response.getOutTradeNo());
                String qrPath = path + "/" + fileName;
                logger.info("filePath:" + qrPath);
                File forder = new File(path);
                if(!forder.exists()) {
                	forder.setWritable(true);
                	forder.mkdirs();
                }
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File qrFile = new File(qrPath);
				try {
					FTPUtil.uploadFile(Lists.newArrayList(qrFile));
				} catch (IOException e) {
					logger.error("上传文件失败", e);
					throw new Exception("上传文件失败!!!");
				}
				qrFile.delete();
				Map<String, String> map = Maps.newHashMap();
				map.put("orderNo", order.getOrderNo().toString());
				map.put("qrPath", PropertiesUtil.getProperty("img.host") + fileName);
                return map;
            case FAILED:
            	logger.error("支付宝预下单失败!!!");
            	throw new Exception("支付宝预下单失败!!!");
            	
            case UNKNOWN:
            	logger.error("系统异常，预下单状态未知!!!");
            	throw new Exception("系统异常，预下单状态未知!!!");

            default:
            	logger.error("不支持的交易状态，交易返回异常!!!");
            	throw new Exception("不支持的交易状态，交易返回异常!!!");
        }
	}
	
	// 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
        	logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
            	logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                    response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

	@Override
	public ResponseService<String> aliCallback(Map<String, String> params) {
		Long orderNo = Long.valueOf(params.get("out_trade_no"));
		String tradeNo = params.get("trade_no");
		String tradeStatus = params.get("trade_status");
		Order order = orderMapper.selectByOrderNo(orderNo);
		if(order == null) return ResponseService.createErrorResponseMessage("非相应的订单 回调忽略");
		if(order.getStatus() >= Constant.PayStatusEnum.PAIED.getCode()) {
			return ResponseService.createSuccessResponseMessage("支付宝重复调用");
		}
		if(Constant.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
			order.setPaymentTime(DateTimeUtil.strToDate(params.get("pmt_payment")));
			order.setStatus(Constant.PayStatusEnum.PAIED.getCode());
			orderMapper.updateByPrimaryKeySelective(order);
		}
		PayInfo payInfo = new PayInfo();
		payInfo.setUserId(order.getUserId());
		payInfo.setOrderNo(order.getOrderNo());
		payInfo.setPayPlatform(Constant.PayPlatformEnum.ALIPAY.getCode());
		payInfo.setPlatformStatus(tradeNo);
		payInfo.setPlatformStatus(tradeStatus);
		payInfoMapper.insert(payInfo);
		return ResponseService.createSuccessResponse();
	}

	@Override
	public ResponseService<Boolean> queryOrderPayStatus(Integer userId, Long orderNo) {
		Order order  = orderMapper.selectByOrderNoAndUserId(userId, orderNo);
		if(order == null) {
			return ResponseService.createSuccessResponseMessage("用户没有该订单");
		}
		if(order.getStatus() >= Constant.PayStatusEnum.PAIED.getCode()) {
			return ResponseService.createSuccessResponse(true);
		}
		return ResponseService.createSuccessResponse(false);
	}

	
	@Override
	public ResponseService<OrderVo> create(Integer userId, Integer shippingId) {
		List<Cart> cartList = cartMapper.getCheckedCartByUserId(userId);
		Long orderNo = generateOrderNo();
		List<OrderItem> orderItemList = Lists.newArrayList();
		for(Cart cart : cartList) {
			Product product = productMapper.selectByPrimaryKey(cart.getProductId());
			OrderItem orderItem = new OrderItem();
			orderItem.setUserId(userId);
			orderItem.setProductId(product.getId());
			orderItem.setOrderNo(orderNo);
			orderItem.setProductName(product.getName());
			orderItem.setProductImage(product.getMainImage());
			orderItem.setCurrentUnitPrice(product.getPrice());
			orderItem.setQuantity(cart.getQuantity());
			orderItem.setTotalPrice(PriceUtil.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
			orderItemList.add(orderItem);
		}
		Order order = new Order();
		order.setOrderNo(Long.valueOf(orderNo));
		order.setPayment(getTotalOrderPrice(orderItemList));
		order.setPaymentType(Constant.PaymentType.ON_LINE.getCode());
		order.setShippingId(shippingId);
		order.setPostage(0);
		order.setUserId(userId);
		order.setStatus(Constant.PayStatusEnum.NO_PAY.getCode());
		
		// 插入订单
		orderMapper.insert(order);
		
		// 插入order item
		orderItemMapper.batchInsert(orderItemList);
		
		// 删除 购物车被选中状态的物品
		for(Cart cart : cartList) {
			cartMapper.deleteByPrimaryKey(cart.getId());
		}
		OrderVo orderVo = assembleOrderVo(order, orderItemList);
		return ResponseService.createSuccessResponse(orderVo);
	}

	private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
		OrderVo orderVo = new OrderVo();
		orderVo.setCloseTime(order.getCloseTime());
		orderVo.setCreateTime(order.getCreateTime());
		orderVo.setEndTime(order.getEndTime());
		orderVo.setOrderNo(order.getOrderNo());
		orderVo.setPayment(order.getPayment());
		orderVo.setPaymentType(order.getPaymentType());
		orderVo.setPostage(order.getPostage());
		orderVo.setSendTime(order.getSendTime());
		orderVo.setShippingId(order.getShippingId());
		orderVo.setStatus(order.getStatus());
		List<OrderItemVo> orderItemVoList = Lists.newArrayList();
		for(OrderItem item : orderItemList) {
			OrderItemVo orderItemVo = new OrderItemVo(item);
			orderItemVoList.add(orderItemVo);
			
		}
		orderVo.setOrderItemVoList(orderItemVoList);
		return orderVo;
	}

	private BigDecimal getTotalOrderPrice(List<OrderItem> orderItemList) {
		BigDecimal totalPrice = new BigDecimal("0");
		for(OrderItem item : orderItemList) {
			totalPrice = PriceUtil.add(totalPrice.doubleValue(), item.getTotalPrice().doubleValue());
		}
		return totalPrice;
	}

	private Long generateOrderNo() {
		Long orderNo = System.currentTimeMillis();
		return Long.valueOf(orderNo + new Random(1000).toString());
	}

}
