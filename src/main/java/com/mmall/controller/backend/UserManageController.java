package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mmall.common.Constant;
import com.mmall.common.ResponseService;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;

@RestController
@RequestMapping("manager/user")
public class UserManageController {
	
	@Autowired
	IUserService iUserService;
	
	@RequestMapping(value = "login.do", method = RequestMethod.POST)
	public ResponseService<User> login(@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password, HttpSession session) {
		ResponseService<User> response = iUserService.login(username, password);
		if (response.isSuccess()) {
			session.setAttribute(Constant.CURRENT_USER, response.getData());
		}
		return response;
	}
	
}
