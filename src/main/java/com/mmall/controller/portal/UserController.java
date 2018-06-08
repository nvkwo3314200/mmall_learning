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
import com.mmall.service.IUserService;

@RestController
@RequestMapping(value = "user")
public class UserController {

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

	@RequestMapping(value = "logout.do", method = RequestMethod.POST)
	public ResponseService<String> logout(HttpSession session) {
		session.removeAttribute(Constant.CURRENT_USER);
		return ResponseService.createSuccessResponseMessage("退出成功");
	}

	@RequestMapping(value = "register.do", method = RequestMethod.POST)
	public ResponseService<String> register(User user) {

		return iUserService.register(user);
	}

	@RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
	public ResponseService<String> checkValid(String str, String type) {
		return iUserService.checkValid(str, type);
	}

	@RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
	public ResponseService<User> getUserInfo(HttpSession session) {
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ResponseService.createErrorResponseMessage("用户未登录,无法获取当前用户信息");
		} else {
			return ResponseService.createSuccessResponse(user);
		}
	}

	@RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
	public ResponseService<String> forgetGetQuestion(String username) {
		return iUserService.forgetGetQuestion(username);
	}

	@RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
	public ResponseService<String> forgetCheckAnswer(String username, String question, String answer) {
		return iUserService.forgetCheckAnswer(username, question, answer);
	}

	@RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
	public ResponseService<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
		return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
	}

	@RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
	public ResponseService<String> resetPassword(String passwordOld, String passwordNew, HttpSession session) {
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if(user == null) return ResponseService.createErrorResponseMessage("请重新登入");
		return iUserService.resetPassword(passwordOld, passwordNew, user);
	}
	
	@RequestMapping(value = "update_information.do", method = RequestMethod.POST)
	public ResponseService<User> updateInformation(User user, HttpSession session) {
		User sessionUser = (User) session.getAttribute(Constant.CURRENT_USER);
		if(sessionUser == null) return ResponseService.createErrorResponseMessage("请重新登入");
		ResponseService<User> response = iUserService.updateInformation(user, sessionUser);
		if(response.isSuccess()) {
			session.setAttribute(Constant.CURRENT_USER, response.getData());
			return ResponseService.createSuccessResponseMessage("更新成功");
		}
		return response;	
	}
	
	@RequestMapping(value = "get_information.do", method = RequestMethod.POST)
	public ResponseService<User> getInformation(HttpSession session) {
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if(user == null) return ResponseService.createErrorResponseMessage("用户未登录,无法获取当前用户信息,status=10,强制登录");
		return ResponseService.createSuccessResponse(user);
	}

}
