package com.mmall.common;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ResponseService<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int status;  // 0表示Success
	private String msg;
	private T data;
	
	private ResponseService(int status) {
		super();
		this.status = status;
	}
	private ResponseService(int status, String msg) {
		super();
		this.status = status;
		this.msg = msg;
	}
	private ResponseService(int status, T data) {
		super();
		this.status = status;
		this.data = data;
	}
	private ResponseService(int status, String msg, T data) {
		super();
		this.status = status;
		this.msg = msg;
		this.data = data;
	}
	
	@JsonIgnore
	public boolean isSuccess() {
		return this.status == ResponseCode.SUCCESS.getCode();
	}
	
	public static <T> ResponseService<T> createSuccessResponse() {
		return new ResponseService<T>(ResponseCode.SUCCESS.getCode());
	}
	
	public static <T> ResponseService<T> createSuccessResponseMessage(String msg) {
		return new ResponseService<T>(ResponseCode.SUCCESS.getCode(), msg);
	}
	//此方法与上面的方法不能重名，如果重名，当泛型为String时,是无法正确定位到该方法的
	public static <T> ResponseService<T> createSuccessResponse(T data) {
		return new ResponseService<T>(ResponseCode.SUCCESS.getCode(), data);
	}
	
	public static <T> ResponseService<T> createSuccessResponse(String msg, T data) {
		return new ResponseService<T>(ResponseCode.SUCCESS.getCode(), msg, data);
	}
	
	public static <T> ResponseService<T> createErrorResponse() {
		return new ResponseService<T>(ResponseCode.ERROR.getCode());
	}
	
	public static <T> ResponseService<T> createErrorResponseMessage(String msg) {
		return new ResponseService<T>(ResponseCode.ERROR.getCode(), msg);
	}
	
	public static <T> ResponseService<T> createErrorResponseCodeMessage(Integer code, String msg) {
		return new ResponseService<T>(code, msg);
	}
	
	public static <T> ResponseService<T> createErrorResponse(T data) {
		return new ResponseService<T>(ResponseCode.ERROR.getCode(), data);
	}
	
	public static <T> ResponseService<T> createErrorResponse(String msg, T data) {
		return new ResponseService<T>(ResponseCode.ERROR.getCode(), msg, data);
	}
	
	public static <T> ResponseService<T> createResponse(Integer code, String msg, T data) {
		return new ResponseService<T>(code, msg, data);
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
