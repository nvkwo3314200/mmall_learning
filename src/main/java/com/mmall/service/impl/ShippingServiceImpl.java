package com.mmall.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseService;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
	
	@Autowired
	ShippingMapper shippingMapper;
	
	@Override
	public ResponseService<Map<String, String>> add(Shipping shipping) {
		int count = shippingMapper.insertSelective(shipping);
		if(count > 0) {
			Map<String, String> map = Maps.newHashMap();
			map.put("shipping", String.valueOf(shipping.getId()));
			return ResponseService.createSuccessResponse("新建地址成功", map);
		}
		return ResponseService.createErrorResponseMessage("新建地址失败");
	}

	@Override
	public ResponseService<String> del(Integer shippingId) {
		int count = shippingMapper.deleteByPrimaryKey(shippingId);
		if(count > 0) {
			return ResponseService.createSuccessResponse("删除地址成功");
		}
		return ResponseService.createErrorResponseMessage("删除地址失败");
	}

	@Override
	public ResponseService<String> update(Shipping shipping) {
		int count = shippingMapper.updateByPrimaryKeySelective(shipping);
		if(count > 0) {
			return ResponseService.createSuccessResponse("更新地址成功");
		}
		return ResponseService.createErrorResponseMessage("更新地址失败");
	}

	@Override
	public ResponseService<Shipping> select(Integer shippingId) {
		Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
		if(shipping != null) {
			return ResponseService.createSuccessResponse(shipping);
		}
		return ResponseService.createSuccessResponseMessage("无对应的地址信息");
	}

	@Override
	public ResponseService<PageInfo<Shipping>> list(Integer pageNum, Integer pageSize, Integer userId) {
		PageHelper.startPage(pageNum, pageSize);
		List<Shipping> list = shippingMapper.selectByUserId(userId);
		PageInfo<Shipping> page = new PageInfo<>(list);
		return ResponseService.createSuccessResponse(page);
	}

}
