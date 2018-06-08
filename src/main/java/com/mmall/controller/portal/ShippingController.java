package com.mmall.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mmall.common.ResponseService;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import com.mmall.service.IUserService;

@RestController
@RequestMapping("shipping")
public class ShippingController {
	
	@Autowired
	IUserService iUserService;
	
	@Autowired
	IShippingService iShippingService;
	
	@RequestMapping(value="add.do", method=RequestMethod.POST)
	/**
	 * 如果前台只传了对应的字段,而不是对应的对象,不应加上@RequestParam, 这样是无法对应上的
	 * @param session
	 * @param shipping
	 * @return
	 */
	public ResponseService<?> add(HttpSession session, Shipping shipping) {
		ResponseService<User> response = iUserService.checkUserLogin(session);
		if(!response.isSuccess()) {
			return response;
		}
		shipping.setUserId(response.getData().getId());
		return iShippingService.add(shipping);
	}
	
	@RequestMapping(value="del.do", method=RequestMethod.POST)
	public ResponseService<?> del(HttpSession session, @RequestParam(value="shippingId")Integer shippingId) {
		ResponseService<User> response = iUserService.checkUserLogin(session);
		if(!response.isSuccess()) {
			return response;
		}
		return iShippingService.del(shippingId);
	}
	
	@RequestMapping(value="update.do", method=RequestMethod.POST)
	public ResponseService<?> update(HttpSession session, Shipping shipping) {
		ResponseService<User> response = iUserService.checkUserLogin(session);
		if(!response.isSuccess()) {
			return response;
		}
		return iShippingService.update(shipping);
	}
	
	@RequestMapping(value="select.do", method=RequestMethod.POST)
	public ResponseService<?> select(HttpSession session, @RequestParam(value="shippingId")Integer shippingId) {
		ResponseService<User> response = iUserService.checkUserLogin(session);
		if(!response.isSuccess()) {
			return response;
		}
		return iShippingService.select(shippingId);
	}
	
	@RequestMapping(value="list.do", method=RequestMethod.POST)
	public ResponseService<?> list(HttpSession session, 
			@RequestParam(value="pageNum", defaultValue = "1")Integer pageNum,
			@RequestParam(value="pageSize", defaultValue = "10")Integer pageSize) {
		ResponseService<User> response = iUserService.checkUserLogin(session);
		if(!response.isSuccess()) {
			return response;
		}
		Integer userId = response.getData().getId();
		return iShippingService.list(pageNum, pageSize, userId);
	}
}
