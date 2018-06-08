package com.mmall.service;

import java.util.Map;

import com.mmall.common.ResponseService;
import com.mmall.vo.OrderVo;

public interface IOrderService {

	ResponseService<Map<String, String>> pay(Integer userId, Long orderNo, String path);

	ResponseService<String> aliCallback(Map<String, String> params);

	ResponseService<Boolean> queryOrderPayStatus(Integer userId, Long orderNo);

	ResponseService<OrderVo> create(Integer id, Integer shipping);
	
}
