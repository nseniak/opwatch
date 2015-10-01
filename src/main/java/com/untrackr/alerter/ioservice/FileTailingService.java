package com.untrackr.alerter.ioservice;

import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.service.ProcessorService;
import com.untrackr.alerter.service.ProfileService;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class FileTailingService implements DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(FileTailingService.class);

	@Autowired
	private ProfileService profileService;

	@Autowired
	private ProcessorService processorService;

	private ThreadPoolExecutor tailingThreadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
			new SynchronousQueue<>(), ThreadUtil.threadFactory("FileTailing"));

	private Map<TailedFile, Thread> tailedFiles = new ConcurrentHashMap<>();

	public void addTailedFile(TailedFile tailedFile) {
		logger.info("Added tailed file: " + tailedFile.getFile());
		Thread thread = ThreadUtil.threadFactory("FileWatching").newThread(() -> tailFile(tailedFile));
		thread.start();
		tailedFiles.put(tailedFile, thread);
	}

	public void removeTailedFile(TailedFile tailedFile) {
		logger.info("Removed tailed file: " + tailedFile.getFile());
		silentlyRemoveTailedFile(tailedFile);
	}

	private void silentlyRemoveTailedFile(TailedFile tailedFile) {
		Thread thread = tailedFiles.remove(tailedFile);
		if (thread != null) {
			thread.interrupt();
		}
	}

	@Override
	public void destroy() throws Exception {
		ThreadUtil.safeExecutorShutdown(tailingThreadPoolExecutor, "FileTailingService", profileService.profile().getExecutorTerminationTimeout());
	}

	public void tailFile(TailedFile tailedFile) {
		try {
			tailedFile.tail();
		} catch (Throwable t) {
			processorService.infrastructureAlert(Alert.Priority.emergency, "Exception while tailing file", tailedFile.getFile().toString(), t);
		}
	}

	public static class MyTailHandler extends TailerListenerAdapter {

		private TailedFile tailedFile;

		public MyTailHandler(TailedFile tailedFile) {
			this.tailedFile = tailedFile;
		}

		@Override
		public void handle(String line) {
			tailedFile.getHandler().handle(line, 0);
		}

	}

}
