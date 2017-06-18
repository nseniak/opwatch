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
public class DirectoryWatchingService implements InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(DirectoryWatchingService.class);

	@Autowired
	private ProcessorService processorService;

	private ScheduledThreadPoolExecutor directoryMonitoringExecutor;

	private Map<WatchedDirectory, ScheduledFuture<?>> watchedDirectories = new ConcurrentHashMap<>();

	public void addWatchedDirectory(WatchedDirectory watchedDirectory) {
		logger.info("Added watched directory: " + watchedDirectory.getDirectory());
		ScheduledFuture<?> future = directoryMonitoringExecutor.scheduleAtFixedRate(() -> watchDirectory(watchedDirectory),
				0, processorService.config().fileWatchingCheckDelay(), TimeUnit.MILLISECONDS);
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
		ThreadUtil.safeExecutorShutdown(directoryMonitoringExecutor, "DirectoryWatchingService", processorService.config().executorTerminationTimeout());
	}

	public void watchDirectory(WatchedDirectory watchedDirectory) {
		processorService.withExceptionHandling("error watching directory " + watchedDirectory.getDirectory().toString(),
				GlobalExecutionScope::new,
				watchedDirectory::watch);
	}

}
