package com.untrackr.alerter.ioservice;

import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.processor.common.ApplicationInterruptedException;
import com.untrackr.alerter.processor.common.GlobalExecutionScope;
import com.untrackr.alerter.service.ProcessorService;
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
		ThreadUtil.safeExecutorShutdown(tailingThreadPoolExecutor, "FileTailingService", processorService.config().executorTerminationTimeout());
	}

	public void tailFile(TailedFile tailedFile) {
		try {
			processorService.withExceptionHandling("error tailing file " + tailedFile.getFile().toString(),
					GlobalExecutionScope::new,
					tailedFile::tail);
		} catch (ApplicationInterruptedException e) {
			// Exit
		}
	}

}
