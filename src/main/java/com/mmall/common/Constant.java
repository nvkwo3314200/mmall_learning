package com.mmall.common;

import java.util.Set;

import com.google.common.collect.Sets;

public class Constant {
	public static final String CURRENT_USER = "current_user";
	
	public static final Set<String> PRODUCT_ORDER_BY_SET = Sets.newHashSet("price_asc", "price_desc", "name_asc", "name_desc");
	
	// 在常量类里面用内部接口，达到一个轻量级的分组
	public interface Role{
		int NORMAL = 0; // 普通用户
		int ADMIN = 1;	// 管理员用户
	}
	
	public interface CartProPerties {
		String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
		String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
		
		Integer CHECKED = 1;
		Integer UNCHECKED = 0;
	}
	
	// 注册明验证的对应类型
	public interface RegisterValidType{
		String USERNAME = "username";
		String EMAIL = "email";
	}
	
	public enum SaleStatus {
		ON_SALE(1, "在售");
		private SaleStatus(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		private int code;
		private String desc;
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
	
	public enum PayStatusEnum {
		CANCELED(0, "已取消"),
		NO_PAY(10, "未支付"),
		PAIED(20, "已支付"),
		SHIPING(30, "已发货"),
		FINISHED(40, "已完成"),
		CLOSED(50, "已关闭");
		
		private PayStatusEnum(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		private int code;
		private String desc;
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
	
	public interface AlipayCallback{
		String RESPONSE_SUCCESS = "success";
		String RESPONSE_FAILED = "failed";
		
		String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
		String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
	}
	
	public enum PayPlatformEnum{
		ALIPAY(1, "支付宝");
		private PayPlatformEnum(int code, String name) {
			this.code = code;
			this.name = name;
		}
		private int code;
		private String name;
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public enum PaymentType{
		ON_LINE(1, "在线支付");
		private PaymentType(int code, String name) {
			this.code = code;
			this.name = name;
		}
		private int code;
		private String name;
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}
