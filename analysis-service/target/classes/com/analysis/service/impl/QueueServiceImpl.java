package com.analysis.service.impl;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Service;

import com.analysis.service.intf.QueueService;

@Service
public class QueueServiceImpl implements QueueService {

	private static volatile ConcurrentLinkedQueue<String> conLinkedQueue = null;
	
	private static volatile LinkedBlockingQueue<String> linkBlockQueue = null;

	@Override
	public String getFileName() {
		return conLinkedQueue.remove();
	}

	@Override
	public boolean saveFileName(String fileName) {
		return conLinkedQueue.add(fileName);
	}

	@Override
	public Integer getQueueSize() {
		return conLinkedQueue.size();
	}

	/**
	 * 获取非阻塞队列单例
	 * @return
	 */
	public static ConcurrentLinkedQueue<String> getInstance() {
		if (conLinkedQueue == null) {
			synchronized (QueueServiceImpl.class) {
				if (conLinkedQueue == null) {
					conLinkedQueue = new ConcurrentLinkedQueue<String>();
				}
			}
		}
		return conLinkedQueue;
	}
	
	/**
	 * 获取阻塞队列单例
	 * @return
	 */
	public static LinkedBlockingQueue<String> getInstans() {
		if (linkBlockQueue == null) {
			synchronized (QueueServiceImpl.class) {
				if (linkBlockQueue == null) {
					linkBlockQueue = new LinkedBlockingQueue<String>();
				}
			}
		}
		return linkBlockQueue;
	}
	
}
