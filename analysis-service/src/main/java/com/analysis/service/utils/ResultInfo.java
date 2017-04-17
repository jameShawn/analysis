package com.analysis.service.utils;

import java.io.Serializable;

/**
 * 返回结果包装类
 * @author jiangx
 *
 * @param <T>
 */
public class ResultInfo<T> implements Serializable{

	private static final long serialVersionUID = 7078467000832036455L;

	private boolean isSuccess;
	
	private String errorCode;
	
	private String errorMessage;
	
	private T data;

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
