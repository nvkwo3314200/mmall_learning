package com.mmall.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mmall.common.Constant;
import com.mmall.common.ResponseService;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.service.IUserService;

@RestController
@RequestMapping("cart")
public class CartController {
	
	@Autowired
	IUserService IUserService;
	
	@Autowired
	ICartService iCartService;
	
	@RequestMapping(value= "add.do", method = RequestMethod.POST)
	public ResponseService<?> add(HttpSession session, 
			@RequestParam(value="productId")Integer productId, 
			@RequestParam(value="count")Integer count) {
		ResponseService<User> userResponse = IUserService.checkUserLogin(session);
		if(!userResponse.isSuccess()) {
			return userResponse;
		}
		return iCartService.add(userResponse.getData().getId(), productId, count);
	}
	
	@RequestMapping(value= "update.do", method = RequestMethod.POST)
	public ResponseService<?> update(HttpSession session, 
			@RequestParam(value="productId")Integer productId, 
			@RequestParam(value="count")Integer count) {
		ResponseService<User> userResponse = IUserService.checkUserLogin(session);
		if(!userResponse.isSuccess()) {
			return userResponse;
		}
		return iCartService.update(userResponse.getData().getId(), productId, count);
	}
	
	@RequestMapping(value= "delete_product.do", method = RequestMethod.POST)
	public ResponseService<?> deleteProduct(HttpSession session, 
			@RequestParam(value="productIds")String productIds) {
		ResponseService<User> userResponse = IUserService.checkUserLogin(session);
		if(!userResponse.isSuccess()) {
			return userResponse;
		}
		return iCartService.del(userResponse.getData().getId(), productIds);
	}
	
	@RequestMapping(value= "select.do", method = RequestMethod.POST)
	public ResponseService<?> select(HttpSession session, 
			@RequestParam(value="productId")Integer productId) {
		ResponseService<User> userResponse = IUserService.checkUserLogin(session);
		if(!userResponse.isSuccess()) {
			return userResponse;
		}
		return iCartService.selectOrUnselect(userResponse.getData().getId(), productId, Constant.CartProPerties.CHECKED);
	}
	
	@RequestMapping(value= "un_select.do", method = RequestMethod.POST)
	public ResponseService<?> unselect(HttpSession session, 
			@RequestParam(value="productId")Integer productId) {
		ResponseService<User> userResponse = IUserService.checkUserLogin(session);
		if(!userResponse.isSuccess()) {
			return userResponse;
		}
		return iCartService.selectOrUnselect(userResponse.getData().getId(), productId, Constant.CartProPerties.UNCHECKED);
	}
	
	@RequestMapping(value= "select_all.do", method = RequestMethod.POST)
	public ResponseService<?> selectAll(HttpSession session) {
		ResponseService<User> userResponse = IUserService.checkUserLogin(session);
		if(!userResponse.isSuccess()) {
			return userResponse;
		}
		return iCartService.selectOrUnselect(userResponse.getData().getId(), null, Constant.CartProPerties.CHECKED);
	}
	
	@RequestMapping(value= "un_select_all.do", method = RequestMethod.POST)
	public ResponseService<?> unselectAll(HttpSession session) {
		ResponseService<User> userResponse = IUserService.checkUserLogin(session);
		if(!userResponse.isSuccess()) {
			return userResponse;
		}
		return iCartService.selectOrUnselect(userResponse.getData().getId(), null, Constant.CartProPerties.UNCHECKED);
	}
	
	@RequestMapping(value= "get_cart_product_count.do", method = RequestMethod.POST)
	public ResponseService<Integer> getCartProductCount(HttpSession session) {
		ResponseService<User> userResponse = IUserService.checkUserLogin(session);
		if(!userResponse.isSuccess()) {
			return ResponseService.createSuccessResponse(0);
		}
		return iCartService.getCartProductCount(userResponse.getData().getId());
	}
	
	@RequestMapping(value= "list.do", method = RequestMethod.POST)
	public ResponseService<?> list(HttpSession session) {
		ResponseService<User> userResponse = IUserService.checkUserLogin(session);
		if(!userResponse.isSuccess()) {
			return userResponse;
		}
		return iCartService.listCart(userResponse.getData().getId());
	}
}
