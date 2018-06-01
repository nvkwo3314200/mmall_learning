package com.mmall.common;

public enum ResponseCode {
	SUCCESS(0, "SUCCESS"),
	ERROR(1, "ERROR"),
	ARGUMENT_ILLEGAL(2, "ARGUMENT_ILLEGAL"),
	WRONG_PASSWORD(3, "WRONG_PASSWORD"),
	NON_LOGIN(10, "NON_LOGIN");
	
	private int code;
	private String desc;
	
	private ResponseCode(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

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
