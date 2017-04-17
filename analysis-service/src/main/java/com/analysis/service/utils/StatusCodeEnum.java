package com.analysis.service.utils;

/**
 * 成功失败状态码枚举类
 * @author jiangx
 *
 */
public enum StatusCodeEnum {

	SUCCESS("成功","0"),FAILURE("失败","1");
	
	private String description;
	
	private String errorCode;

	private StatusCodeEnum(String description, String errorCode) {
		this.description = description;
		this.errorCode = errorCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
