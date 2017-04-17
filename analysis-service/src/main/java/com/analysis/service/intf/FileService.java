package com.analysis.service.intf;

import com.analysis.service.utils.ResultInfo;

/**
 * 文件处理接口
 * @author jiangx
 *
 */
public interface FileService {

	/**
	 * 文件处理
	 * @param fileName
	 * @return
	 */
	public ResultInfo<String> processFile(String fileName);
	
	/**
	 * 文件删除
	 * @param fileName
	 */
	public void deleteFile(String fileName);
	
	/**
	 * 文件拷贝
	 * @param fileName
	 */
	public void copyFile(String fileName);
	
	/**
	 * 将没有传输成功的json写入到文件
	 * @param json
	 */
	public void writeFile(String json);
}
