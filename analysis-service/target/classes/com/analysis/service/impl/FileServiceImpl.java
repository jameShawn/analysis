package com.analysis.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.analysis.service.constants.Constants;
import com.analysis.service.intf.FileService;
import com.analysis.service.utils.ResultInfo;

@Service
public class FileServiceImpl implements FileService {

	private static ThreadLocal<Integer> countLocal = new ThreadLocal<Integer>();
	
	private Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
	
	@Override
	public ResultInfo<String> processFile(String fileName) {
		ResultInfo<String> resultInfo = new ResultInfo<String>();
		resultInfo.setData(fileName);
		boolean result = false;
		ZipInputStream is = null;
		BufferedReader br = null;
		try {
			// 获取压缩文件
			ZipFile zipFile = new ZipFile(Constants.FILE_URL + fileName
					+ Constants.FILE_PATTERN);
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(Constants.DECOMPRESS_PASSWORD);
			}
			// 获取文件头信息
			@SuppressWarnings("rawtypes")
			List fileHeaderList = zipFile.getFileHeaders();
			for (int i = 0; i < fileHeaderList.size(); i++) {
				FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
				if (fileHeader != null) {
					// zip输入流
					is = zipFile.getInputStream(fileHeader);
					br = new BufferedReader(new InputStreamReader(is));
					// 将当前线程传输次数设置为0
					countLocal.set(0);
					String json = null;
					while (StringUtils.isNotEmpty(json = br.readLine())) {
						// 读取zip文件内容,传到大数据
						result = transferData(json);
						System.out.println(json);
						// 判断有没有发送成功
						while (!result) {
							// 如果当前的发送次数超过三次
							if (countLocal.get() > Constants.TRY_TIMES) {
								writeFile(json);
								countLocal.set(0);
								break;
							}
							result = transferData(json);
							// 对当前线程发送次数加一
							countLocal.set(countLocal.get() + 1);
						}
					}
				}
			}
		} catch (ZipException e) {
			log.error("",e);
		} catch (IOException e) {
			log.error("",e);
		} finally {
			try {
				is.close();
				br.close();
			} catch (IOException e) {
				log.error("",e);
			}
		}
		resultInfo.setIsSuccess(result);
		return resultInfo;
	}

	/**
	 * 将json传给大数据
	 * 
	 * @param json
	 * @return
	 */
	private boolean transferData(String json) {
		return false;
	}

	@Override
	public void deleteFile(String fileName) {
		File file = new File(Constants.FILE_URL + fileName
				+ Constants.FILE_PATTERN);
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	public void copyFile(String fileName) {
		FileInputStream fin = null;
		FileOutputStream fout = null;
		try {
			fin = new FileInputStream(new File(Constants.FILE_URL + fileName
					+ Constants.FILE_PATTERN));
			fout = new FileOutputStream(new File(Constants.FAIL_FILE_URL
					+ fileName + Constants.FILE_PATTERN));
			int bytesRead;
			byte[] buf = new byte[4 * 1024];
			while ((bytesRead = fin.read(buf)) != -1) {
				fout.write(buf, 0, bytesRead);
			}
		} catch (FileNotFoundException e) {
			log.error("",e);
		} catch (IOException e) {
			log.error("",e);
		} finally {
			try {
				fout.flush();
				fout.close();
				fin.close();
			} catch (IOException e) {
				log.error("",e);
			}
		}
	}

	@Override
	public void writeFile(String json) {
		FileWriter fileWriter = null;
		String fileName = createFileName();
		BufferedWriter writer = null;
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			fileWriter = new FileWriter(Constants.FAIL_FILE_URL + fileName
					+ Constants.FAIL_FILE_PATTERN, true);
			writer = new BufferedWriter(fileWriter);
			writer.write(json);
			writer.newLine();
		} catch (IOException e) {
			log.error("",e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				log.error("",e);
			}
		}
	}

	/**
	 * 根据日期生成文件名
	 * 
	 * @return fileName
	 */
	private String createFileName() {
		Date date = new Date();
		return date.getTime() / Constants.DAY_OF_TIME + "";
	}

}
