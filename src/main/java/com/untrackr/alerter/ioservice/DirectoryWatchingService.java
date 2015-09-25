package com.untrackr.alerter.ioservice;

import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.service.ProcessorService;
import com.untrackr.alerter.service.ProfileService;
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
public class DirectoryWatchingService implements InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(DirectoryWatchingService.class);

	@Autowired
	private ProfileService profileService;

	@Autowired
	private ProcessorService processorService;

	private ScheduledThreadPoolExecutor directoryMonitoringExecutor;

	private Map<WatchedDirectory, ScheduledFuture<?>> watchedDirectories = new ConcurrentHashMap<>();

	public void addWatchedDirectory(WatchedDirectory watchedDirectory) {
		logger.info("Added watched directory: " + watchedDirectory.getDirectory());
		ScheduledFuture<?> future = directoryMonitoringExecutor.scheduleAtFixedRate(() -> watchDirectory(watchedDirectory),
				0, profileService.profile().getFileWatchingCheckDelay(), TimeUnit.MILLISECONDS);
		watchedDirectories.put(watchedDirectory, future);
	}

	public void removeWatchedDirectory(WatchedDirectory watchedDirectory) {
		logger.info("Removed watched directory: " + watchedDirectory.getDirectory());
		ScheduledFuture<?> future = watchedDirectories.remove(watchedDirectory);
		if (future != null) {
			future.cancel(false);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		directoryMonitoringExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtil.threadFactory("DirectoryWatching"));
	}

	@Override
	public void destroy() throws Exception {
		ThreadUtil.safeExecutorShutdown(directoryMonitoringExecutor, "DirectoryWatchingService", profileService.profile().getExecutorTerminationTimeout());
	}

	public void watchDirectory(WatchedDirectory watchedDirectory) {
		try {
			watchedDirectory.watch();
		} catch (Throwable t) {
			processorService.infrastructureAlert(Alert.Priority.emergency, "Exception while watching directory", watchedDirectory.getDirectory().toString(), t);
		}
	}

}
