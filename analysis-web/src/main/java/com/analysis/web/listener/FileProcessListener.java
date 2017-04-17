package com.analysis.web.listener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.analysis.service.impl.ScheduleServiceImpl;

public class FileProcessListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.execute(new ScheduleServiceImpl());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

}
