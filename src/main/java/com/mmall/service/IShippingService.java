package com.mmall.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseService;
import com.mmall.pojo.Shipping;

public interface IShippingService {
	
	public ResponseService<Map<String, String>> add(Shipping shipping);

	public ResponseService<String> del(Integer shippingId);

	public ResponseService<String> update(Shipping shipping);

	public ResponseService<Shipping> select(Integer shippingId);

	public ResponseService<PageInfo<Shipping>> list(Integer pageNum, Integer pageSize, Integer userId);
	
}
