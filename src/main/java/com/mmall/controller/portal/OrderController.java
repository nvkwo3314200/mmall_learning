package com.mmall.controller.portal;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ResponseService;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;

@RestController
@RequestMapping("order")
public class OrderController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IOrderService iOrderService;
	
	
	//create.do
	@RequestMapping(value="create.do", method=RequestMethod.POST)
	public ResponseService<?> pay(HttpSession session, Integer shipping) {
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if(user == null) {
			return ResponseService.createErrorResponseCodeMessage(ResponseCode.NON_LOGIN.getCode(), ResponseCode.NON_LOGIN.getDesc());
		}
		return iOrderService.create(user.getId(), shipping);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value="pay.do", method=RequestMethod.POST)
	public ResponseService<?> pay(HttpSession session, HttpServletRequest request, Long orderNo) {
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if(user == null) {
			return ResponseService.createErrorResponseCodeMessage(ResponseCode.NON_LOGIN.getCode(), ResponseCode.NON_LOGIN.getDesc());
		}
		
		String path = request.getServletContext().getRealPath("upload");
		return iOrderService.pay(user.getId(), orderNo, path);
	}
	
	@RequestMapping(value="alipay_callback.do")
	public Object alipayCallback(HttpServletRequest request) {
		Map<String, String> params = Maps.newHashMap();
		for(Iterator<String> iter = request.getParameterMap().keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			String[] values = request.getParameterMap().get(key);
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < values.length; i ++) {
				 sb.append((i == 0)? "":"&").append(values[i]);  
			}
			params.put(key, sb.toString());
		}
		
		logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
		String tradeStatus = params.get("trade_status");
		if(tradeStatus == null || tradeStatus.equals(Constant.AlipayCallback.TRADE_STATUS_WAIT_BUYER_PAY)) {
			logger.info("等待付款");
			return "ok";
		}
		 
		params.remove("sign_type");
		try {
			boolean alipayRsaCheckV2 = AlipaySignature.rsaCheckV2(params, Configs.getPublicKey(), "utf-8", Configs.getSignType());
			if(!alipayRsaCheckV2) {
				return ResponseService.createErrorResponseMessage("非法请求,验证不通过,再恶意请求我就报警找网警了");
			}
		} catch (AlipayApiException e) {
			logger.error("支付宝验证回调异常",e);
		}
		
		
		// TODO 验证数据的下确性
		
		
		ResponseService<String> response = iOrderService.aliCallback(params);
		
		if(response.isSuccess()){
			return Constant.AlipayCallback.RESPONSE_SUCCESS;
	    }
	    return Constant.AlipayCallback.RESPONSE_FAILED;
	}
	
	@RequestMapping("query_order_pay_status.do")
    public ResponseService<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Constant.CURRENT_USER);
        if(user ==null){
            return ResponseService.createErrorResponseCodeMessage(ResponseCode.NON_LOGIN.getCode(),ResponseCode.NON_LOGIN.getDesc());
        }
        ResponseService<Boolean> responseService = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(responseService.isSuccess()){
            return ResponseService.createSuccessResponse(true);
        }
        return ResponseService.createSuccessResponse(false);
    }
}
