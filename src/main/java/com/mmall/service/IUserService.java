package com.mmall.service;

import com.mmall.common.ResponseService;
import com.mmall.pojo.User;

public interface IUserService {
	
	public ResponseService<User> login(String username, String password);

	public ResponseService<String> register(User user);

	public ResponseService<String> checkValid(String str, String type);

	public ResponseService<String> forgetGetQuestion(String username);

	public ResponseService<String> forgetCheckAnswer(String username, String question, String answer);

	public ResponseService<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

	public ResponseService<String> resetPassword(String passwordOld, String passwordNew, User user);

	public ResponseService<User> updateInformation(User user, User sessionUser);

	public ResponseService<String> checkUserPower(User user); 
}
