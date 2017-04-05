package com.untrackr.alerter.ioservice;

import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.processor.common.GlobalExecutionContext;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class FileWatchingService implements InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(FileWatchingService.class);

	@Autowired
	private ProcessorService processorService;

	private ScheduledThreadPoolExecutor fileMonitoringExecutor;

	private Map<WatchedFile, ScheduledFuture<?>> watchedFiles = new ConcurrentHashMap<>();

	public void addWatchedFile(WatchedFile watchedFile) {
		logger.info("Added watched file: " + watchedFile.getFile());
		ScheduledFuture<?> future = fileMonitoringExecutor.scheduleAtFixedRate(() -> watchFile(watchedFile),
				0, processorService.config().fileWatchingCheckDelay(), TimeUnit.MILLISECONDS);
		watchedFiles.put(watchedFile, future);
	}

	public void removeWatchedFile(WatchedFile watchedFile) {
		logger.info("Removed watched file: " + watchedFile.getFile());
		ScheduledFuture<?> future = watchedFiles.remove(watchedFile);
		if (future != null) {
			future.cancel(false);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		fileMonitoringExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtil.threadFactory("FileWatching"));
	}

	@Override
	public void destroy() throws Exception {
		ThreadUtil.safeExecutorShutdown(fileMonitoringExecutor, "FileWatchingService", processorService.config().executorTerminationTimeout());
	}

	public void watchFile(WatchedFile watchedFile) {
		processorService.withExceptionHandling("error watching file " + watchedFile.getFile().toString(),
				new GlobalExecutionContext(),
				watchedFile::watch);
	}

}
