package com.analysis.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.analysis.service.constants.Constants;
import com.analysis.service.impl.QueueServiceImpl;
import com.analysis.service.utils.ReadProperty;
import com.analysis.service.utils.ResultInfo;
import com.analysis.service.utils.StatusCodeEnum;

@Controller
@RequestMapping("/")
public class FileUploadController {
	
	private Logger log = LoggerFactory.getLogger(FileUploadController.class);

	/**
	 * 上传文件
	 * 
	 * @param newFileName
	 * @param filedata
	 */
	@RequestMapping(value = "/app/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public ResultInfo<String> saveFile(
			@RequestParam(value = "logFileName", required = false) MultipartFile logFile,
			HttpServletRequest request) {

		ReadProperty readProperty = new ReadProperty();
		// 构建文件目录
		String fileUrl = readProperty.getString("file.url");
		File fileDir = new File(fileUrl);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		ResultInfo<String> resultInfo = new ResultInfo<String>();
		// 源文件名
		String origFileName = null;
		// 判断获取的信息是不是为空
		if (null != logFile) {
			origFileName = logFile.getOriginalFilename();
			log.info(origFileName);
		} else {
			resultInfo.setErrorCode(StatusCodeEnum.FAILURE.getErrorCode());
			resultInfo.setErrorMessage("获取文件失败");
			resultInfo.setData(null);
			return resultInfo;
		}
		// 判断当前上传的是不是zip
		String[] logName = origFileName.split("\\.");
		FileOutputStream out = null;

		if (null != logName && logName.length == 2
				&& Constants.FILE_SUFFIX.equals(logName[1])
				&& logFile.getSize() <= Constants.MAX_FILE_SIZE) {
			// 生成文件名称,通过UUID
			String fileName = UUID.randomUUID().toString().replaceAll("-", "");
			try {
				out = new FileOutputStream(fileUrl + "\\" + fileName
						+ Constants.POINT + logName[1]);
				// 写入文件
				out.write(logFile.getBytes());
				resultInfo.setErrorCode(StatusCodeEnum.SUCCESS.getErrorCode());
				resultInfo.setErrorMessage("成功");
				resultInfo.setData(fileName);
			} catch (IOException e) {
				log.error("",e);
				resultInfo.setErrorCode(StatusCodeEnum.FAILURE.getErrorCode());
				resultInfo.setErrorMessage("获取文件失败");
				resultInfo.setData(null);
				return resultInfo;
			} finally {
				// 关闭流
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					log.error("",e);
				}
			}
			// 文件名称放入队列
			// 阻塞队列
			LinkedBlockingQueue<String> blockQueue = QueueServiceImpl
					.getInstans();
			try {
				blockQueue.put(fileName);
			} catch (InterruptedException e) {
				log.error("",e);
			}

		} else {
			resultInfo.setErrorCode(StatusCodeEnum.FAILURE.getErrorCode());
			resultInfo.setErrorMessage("文件不正确");
			resultInfo.setData(null);
		}
		return resultInfo;
	}
}
