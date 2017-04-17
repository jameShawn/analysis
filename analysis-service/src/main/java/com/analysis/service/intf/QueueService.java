package com.analysis.service.intf;

/**
 * 队列处理调用接口
 * @author jiangx
 *
 */
public interface QueueService {

	/**
	 * 获取文件名称
	 * @return
	 */
	public String getFileName();

	/**
	 * 保存文件名称
	 * @param fileName
	 * @return
	 */
	public boolean saveFileName(String fileName);

	/**
	 * 获取队列大小
	 * @return
	 */
	public Integer getQueueSize();
}
