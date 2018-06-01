package com.mmall.common;

public class Constant {
	public static final String CURRENT_USER = "current_user";
	
	
	// 在常量类里面用内部接口，达到一个轻量级的分组
	public interface Role{
		int NORMAL = 0; // 普通用户
		int ADMIN = 1;	// 管理员用户
	}
	
	// 注册明验证的对应类型
	public interface RegisterValidType{
		String USERNAME = "username";
		String EMAIL = "email";
	}
}
