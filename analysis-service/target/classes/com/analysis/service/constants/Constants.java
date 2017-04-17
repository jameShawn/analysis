package com.analysis.service.constants;

public class Constants {

	/**
	 * 文件上传的路径
	 */
	public static final String FILE_URL = "E:/file/zip/";
	
	/**
	 * 解压后的文件路径
	 */
	public static final String FILE_UNZIP_URL = "E:/file/unzip/";
	
	/**
	 * 解析或者失败的文件URL
	 */
	public static final String FAIL_FILE_URL = "E:/file/fail/";
	
	/**
	 * 文件格式
	 */
	public static final String FILE_PATTERN = ".zip";
	
	/**
	 * 分隔符点
	 */
	public static final String POINT = ".";
	
	/**
	 * 上传文件的后缀
	 */
	public static final String FILE_SUFFIX = "zip";
	
	/**
	 * 失败文件格式
	 */
	public static final String FAIL_FILE_PATTERN = ".log";
	
	/**
	 * 调用接口重试次数
	 */
	public static final int TRY_TIMES = 3;
	
	/**
	 * 启动的线程数量
	 */
	public static final int THREAD_COUNT = 5;
	
	/**
	 * 一天毫秒数
	 */
	public static final int DAY_OF_TIME = 86400 * 1000;
	
	/**
	 * 上传最大的文件大小
	 */
	public static final int MAX_FILE_SIZE = 300 * 1024;
	
	/**
	 * 延迟时间
	 */
	public static final long DELAY_TIME = 5000;
	
	/**
	 * 解压密码
	 */
	public static final String DECOMPRESS_PASSWORD = "123456";
}
