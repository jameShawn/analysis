package com.analysis.service.job;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.analysis.service.constants.Constants;
import com.analysis.service.impl.FileServiceImpl;
import com.analysis.service.impl.QueueServiceImpl;
import com.analysis.service.utils.ResultInfo;

public class FileProcessJob{

	ThreadLocal<Integer> countLocal = new ThreadLocal<Integer>();

	private static ExecutorService fixedThreadPool = Executors
			.newFixedThreadPool(Constants.THREAD_COUNT);

	/**
	 * 文件处理整体流程
	 * 
	 * @param fileName
	 */
	public void processFile(String fileName) {
		// 判断取得的fileName是不是为空
		if (StringUtils.isNotEmpty(fileName)) {
			FileServiceImpl fileService = new FileServiceImpl();
			// 从本地取得文件流,解析文件传给大数据
			ResultInfo<String> resultInfo = fileService.processFile(fileName);
			countLocal.set(0);
			// 判断是不是成功
			while (!resultInfo.isSuccess()) {
				if (countLocal.get() > Constants.TRY_TIMES) {
					fileService.copyFile(fileName);
					countLocal.set(0);
					break;
				}
				resultInfo = fileService.processFile(fileName);
				countLocal.set(countLocal.get() + 1);
			}
			// 删除本地临时文件
			fileService.deleteFile(fileName);
		}
	}

	/**
	 * 取得当前队列的文件名称
	 * 
	 * @return
	 */
	private synchronized String getFileName() {
		ConcurrentLinkedQueue<String> conLinkedQueue = QueueServiceImpl
				.getInstance();
		String fileName = null;
		if (null != conLinkedQueue && conLinkedQueue.size() > 0) {
			fileName = conLinkedQueue.remove();
		}
		return fileName;
	}

	public void threadProcess() {
		final FileProcessJob fpj = new FileProcessJob();
		ConcurrentLinkedQueue<String> conLinkedQueue = QueueServiceImpl
				.getInstance();
		while (null != conLinkedQueue && conLinkedQueue.size() > 0) {
			fixedThreadPool.execute(new Runnable() {
				public void run() {
					String fileName = fpj.getFileName();
					Thread thread = Thread.currentThread();
					System.out.println(thread.getId());
					System.out.println(thread.getName());
					System.out.println(fileName);
					fpj.processFile(fileName);
					System.out.println(thread.isInterrupted());
					System.out.println("1");
				}
			});
		}
	}

}
