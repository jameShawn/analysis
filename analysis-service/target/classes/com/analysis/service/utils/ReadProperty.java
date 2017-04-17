package com.analysis.service.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取配置文件
 * 
 * @author jiangx
 * 
 */
public class ReadProperty {
	
	private Logger log = LoggerFactory.getLogger(ReadProperty.class);

	Properties properties = null;

	public ReadProperty() {
		super();
		InputStream is = null;
		is = getClass().getResourceAsStream("/file.properties");
		properties = new Properties();
		try {
			properties.load(is);
		} catch (Exception e) {
			log.error("读取配置文件错误", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				log.error("流关闭错误", e);
			}
		}
	}

	public String getString(String key) {
		return properties.getProperty(key);
	}

	public Long getLong(String key) {
		String value = properties.getProperty(key);
		Long longValue = Long.parseLong(value);
		return longValue;
	}

	public Integer getInt(String key) {
		String value = properties.getProperty(key);
		Integer longValue = Integer.parseInt(value);
		return longValue;
	}

	public Boolean getBoolean(String key) {
		String value = properties.getProperty(key);
		Boolean longValue = Boolean.parseBoolean(value);
		return longValue;
	}
}
