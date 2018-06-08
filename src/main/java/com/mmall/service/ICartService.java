package com.mmall.service;

import com.mmall.common.ResponseService;
import com.mmall.vo.CartVo;

public interface ICartService {
	
	ResponseService<CartVo> add(Integer userId, Integer productId, Integer count);

	ResponseService<CartVo> update(Integer userId, Integer productId, Integer count);

	ResponseService<CartVo> del(Integer userId, String productIds);

	ResponseService<CartVo> selectOrUnselect(Integer userId, Integer productId, Integer checked);

	ResponseService<Integer> getCartProductCount(Integer userId);

	ResponseService<CartVo> listCart(Integer userId);
}
