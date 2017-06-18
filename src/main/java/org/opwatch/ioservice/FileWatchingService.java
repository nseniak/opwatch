/*
 * Copyright (c) 2016-2017 by OMC Inc and other Opwatch contributors
 *
 * Licensed under the Apache License, Version 2.0  (the "License").  You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.opwatch.ioservice;

import org.opwatch.common.ThreadUtil;
import org.opwatch.processor.common.GlobalExecutionScope;
import org.opwatch.service.ProcessorService;
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
				GlobalExecutionScope::new,
				watchedFile::watch);
	}

}
