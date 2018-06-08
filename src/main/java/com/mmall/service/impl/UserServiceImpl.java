package com.mmall.service.impl;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ResponseService;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
	
	@Autowired
	UserMapper userMapper;
	
	@Override
	public ResponseService<User> login(String username, String password) {
		int count = userMapper.checkUserExist(username);
		if(count == 0) {
			return ResponseService.createErrorResponseMessage("用户名不存在");
		}
		 //  md5 encrypt password
		String md5Passwrod = MD5Util.MD5EncodeUtf8(password);
		User user = userMapper.checkLogin(username, md5Passwrod);
		if(user == null) return ResponseService.createErrorResponseMessage("用户密码错误");
		user.setPassword(null);
		return ResponseService.createSuccessResponse(user);
	}

	@Override
	public ResponseService<String> register(User user) {
		if(StringUtils.isNotBlank(user.getUsername())) {
			ResponseService<String> validResponse = this.checkValid(user.getUsername(), Constant.RegisterValidType.USERNAME);
			if(!validResponse.isSuccess()) {
				return validResponse;
			}
		}
		
		if(StringUtils.isNotBlank(user.getEmail())) {
			ResponseService<String> validResponse = this.checkValid(user.getEmail(), Constant.RegisterValidType.EMAIL);
			if(!validResponse.isSuccess()) {
				return validResponse;
			}
		}
		
		String md5Password = MD5Util.MD5EncodeUtf8(user.getPassword());
		user.setPassword(md5Password);
		user.setRole(Constant.Role.NORMAL); // 开始注册的都是普通用户
		
		int count = userMapper.insertSelective(user);
		if(count > 0) {
			return ResponseService.createSuccessResponseMessage("注册成功");
		}
		
		return ResponseService.createErrorResponseMessage("注册失败");
	}

	@Override
	public ResponseService<String> checkValid(String str, String type) {
		if(StringUtils.isBlank(str)) {
			return ResponseService.createErrorResponseMessage("被检验数据不能为空");
		}
		if(StringUtils.isNotBlank(type)) {
			if(Constant.RegisterValidType.USERNAME.equals(type)) {
				int count = userMapper.checkUserExist(str);
				if(count > 0) {
					return ResponseService.createErrorResponseMessage("用户已存在");
				}
			}
			
			if(Constant.RegisterValidType.EMAIL.equals(type)) {
				int count = userMapper.checkEmail(str);
				if(count > 0) {
					return ResponseService.createErrorResponseMessage("用户邮箱已存在");
				}
			}
		}
		else {
			return ResponseService.createErrorResponseMessage("校验类型不能为空");
		}
		
		return ResponseService.createSuccessResponseMessage("校验成功");
	}

	@Override
	public ResponseService<String> forgetGetQuestion(String username) {
		if(StringUtils.isNotBlank(username)) {
			ResponseService<String> validResponse = this.checkValid(username, Constant.RegisterValidType.USERNAME);
			if(!validResponse.isSuccess()) {
				// 表示该用户用效
				String question = userMapper.getQuestionByUsername(username);
				if(StringUtils.isNotBlank(question)) {
					return ResponseService.createSuccessResponse(question);
				}
				else {
					return ResponseService.createErrorResponseMessage("该用户未设置找回密码问题");
				}
			}
			else {
				return ResponseService.createErrorResponseMessage("用户不存在");
			}
		}
		return ResponseService.createErrorResponseMessage("用户名不能为空");
	}

	@Override
	public ResponseService<String> forgetCheckAnswer(String username, String question, String answer) {
		if(StringUtils.isBlank(username)) return ResponseService.createErrorResponseMessage("用户名不能为空");
		int count = userMapper.checkAnswer(username, question, answer);
		if(count > 0) {
			String token = generateToken();
			TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, token);
			return ResponseService.createErrorResponse(token);
		}
		return ResponseService.createErrorResponseMessage("问题答案错误");
	}

	private String generateToken() {
		UUID token = UUID.randomUUID();
		return token.toString();
	}

	@Override
	public ResponseService<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
		if(StringUtils.isBlank(username)) return ResponseService.createErrorResponseMessage("用户名不能为空");
		String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
		if(StringUtils.equals(token, forgetToken)) {
			String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
			int count = userMapper.updatePasswordByUserName(username, md5Password);
			if(count > 0) {
				TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, generateToken());
				return ResponseService.createSuccessResponseMessage("修改密码成功");
			}
			return ResponseService.createErrorResponseMessage("修改密码操作败");
		}
		return ResponseService.createErrorResponseMessage("token已经失效");
	}

	@Override
	public ResponseService<String> resetPassword(String passwordOld, String passwordNew, User user) {
		String md5PasswrodOld = MD5Util.MD5EncodeUtf8(passwordOld);
		int count = userMapper.checkPasswordById(user.getId(), md5PasswrodOld);
		if(count > 0) {
			String md5PasswrodNew = MD5Util.MD5EncodeUtf8(passwordNew);
			int countU = userMapper.updatePasswordByUserName(user.getUsername(), md5PasswrodNew);
			if(countU > 0) return ResponseService.createSuccessResponseMessage("修改密码成功");
		}
		return ResponseService.createErrorResponseMessage("旧密码输入错误");
	}

	@Override
	public ResponseService<User> updateInformation(User user, User sessionUser) {
		int count = userMapper.checkEmailWithId(sessionUser.getId(), user.getEmail());
		if(count > 0) {
			return ResponseService.createErrorResponseMessage("用户邮箱已存在");
		}
		User updateUser = new User();
		updateUser.setId(sessionUser.getId());
		updateUser.setPhone(user.getPhone());
		updateUser.setEmail(user.getEmail());
		updateUser.setQuestion(user.getQuestion());
		updateUser.setAnswer(user.getAnswer());
		int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
		if(updateCount > 0) {
			User lastestUser = userMapper.selectByPrimaryKey(sessionUser.getId());
			if(lastestUser == null) {
				return ResponseService.createErrorResponseMessage("更新成功， 获取用户信息失败");
			}
			lastestUser.setPassword(null);
			return ResponseService.createSuccessResponse(lastestUser);
		}
		return ResponseService.createErrorResponseMessage("更新失败");
	}

	@Override
	public ResponseService<String> checkUserPower(User user) {
		if(user != null) {
			if(Constant.Role.ADMIN == user.getRole()) {
				return ResponseService.createSuccessResponse();
			}
		}
		return ResponseService.createErrorResponse();
	}
	
	@Override
	public ResponseService<User> checkUserLogin(HttpSession session) {
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if(user == null) {
			return ResponseService.createErrorResponseCodeMessage(ResponseCode.NON_LOGIN.getCode(), ResponseCode.NON_LOGIN.getDesc());
		}
		return ResponseService.createSuccessResponse(user);
	}

	@Override
	public ResponseService<String> checkUserPower(HttpSession session) {
		ResponseService<User> responseUser = checkUserLogin(session);
		if(!responseUser.isSuccess()) {
			return ResponseService.createErrorResponseCodeMessage(ResponseCode.NON_LOGIN.getCode(), ResponseCode.NON_LOGIN.getDesc());
		}
		ResponseService<String> response = checkUserPower(responseUser.getData());
		return response;
	}
	
}
