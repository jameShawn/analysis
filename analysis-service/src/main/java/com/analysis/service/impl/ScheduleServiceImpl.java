package com.analysis.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.analysis.service.constants.Constants;
import com.analysis.service.utils.ReadProperty;
import com.analysis.service.utils.ResultInfo;

/**
 * 调度实现类
 * 
 * @author jiangx
 * 
 */
@Service
public class ScheduleServiceImpl implements Runnable {

	private static ExecutorService fixedThreadPool = Executors
			.newFixedThreadPool(Constants.THREAD_COUNT);

	private static ThreadLocal<Integer> countLocal = new ThreadLocal<Integer>();
	
	private Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

	@Override
	public void run() {
		// 不停得取得队列中的内容
		while (true) {
			final String fileName = getFileName();
			fixedThreadPool.execute(new Runnable() {
				public void run() {
					processFile(fileName);
				}
			});
		}
	}

	/**
	 * 文件处理整体流程
	 * 
	 * @param fileName
	 */
	private void processFile(String fileName) {
		// 判断取得的fileName是不是为空
		if (StringUtils.isNotEmpty(fileName)) {
			// 从本地取得文件流,解析文件传给大数据
			processLogFile(fileName);
			// 删除本地临时文件
			deleteFile(fileName);
		}
	}

	/**
	 * 取得当前队列的文件名称
	 * 
	 * @return
	 */
	private synchronized String getFileName() {
		LinkedBlockingQueue<String> conLinkedQueue = QueueServiceImpl
				.getInstans();
		String fileName = null;
		try {
			fileName = conLinkedQueue.take();
		} catch (InterruptedException e) {
			log.error("从队列获取文件名称失败",e);
		}
		return fileName;
	}

	/**
	 * 处理log文件
	 * 
	 * @param fileName
	 * @return
	 */
	private ResultInfo<String> processLogFile(String fileName) {
		ResultInfo<String> resultInfo = new ResultInfo<String>();
		resultInfo.setData(fileName);
		boolean result = false;
		ZipInputStream is = null;
		BufferedReader br = null;
		ReadProperty readProperty = new ReadProperty();
		// 构建文件目录
		String fileUrl = readProperty.getString("file.url");
		try {
			// 获取压缩文件
			ZipFile zipFile = new ZipFile(fileUrl + fileName
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
			log.error("zip解析报错",e);
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

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 */
	private void deleteFile(String fileName) {
		ReadProperty readProperty = new ReadProperty();
		// 构建文件目录
		String fileUrl = readProperty.getString("file.url");
		File file = new File(fileUrl + fileName + Constants.FILE_PATTERN);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 将未传输成功的数据写入到失败文件
	 * 
	 * @param json
	 */
	private void writeFile(String json) {
		FileWriter fileWriter = null;
		String fileName = createFileName();
		BufferedWriter writer = null;
		ReadProperty readProperty = new ReadProperty();
		// 构建文件目录
		String fileUrl = readProperty.getString("fail.file.url");
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			fileWriter = new FileWriter(fileUrl + fileName
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
