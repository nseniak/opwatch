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

package org.opwatch.processor.primitives.consumer.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import com.google.common.io.Files;
import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.consumer.Consumer;
import org.opwatch.service.ProcessorService;
import org.slf4j.LoggerFactory;

public class Log extends Consumer<LogConfig> {

	private String file;
	private FileSize maxSize;
	private FileSize maxTotalSize;
	private int maxHistory;
	private String compression;

	private Logger logger;
	private Level level = Level.INFO;

	public Log(ProcessorService processorService, LogConfig configuration, String name,
						 String file,
						 FileSize maxSize,
						 FileSize maxTotalSize,
						 int maxHistory,
						 String compression) {
		super(processorService, configuration, name);
		this.file = file;
		this.maxSize = maxSize;
		this.maxTotalSize = maxTotalSize;
		this.maxHistory = maxHistory;
		this.compression = compression;
	}

	@Override
	public void start() {
		super.start();
		if (file != null) {
			processorService.withExceptionHandling("cannot initialize logger " + file,
					() -> new ProcessorVoidExecutionScope(this),
					() -> {
						initLogger();
					});
		}
	}

	private void initLogger() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		String extension = Files.getFileExtension(file);
		String dotExtension = (extension.isEmpty()) ? "" : "." + extension;
		String dotCompression = (compression.isEmpty()) ? "" : "." + compression;
		String pattern = file + "-%d{yyyy-MM-dd}.%i" + dotExtension + dotCompression;

		RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
		appender.setContext(loggerContext);
		appender.setFile(file);
		appender.setPrudent(false);

		SizeAndTimeBasedRollingPolicy rollingPolicy = new SizeAndTimeBasedRollingPolicy();
		rollingPolicy.setContext(loggerContext);
		rollingPolicy.setParent(appender);
		rollingPolicy.setFileNamePattern(pattern);
		rollingPolicy.start();

		SizeAndTimeBasedRollingPolicy<ILoggingEvent> triggeringPolicy = new SizeAndTimeBasedRollingPolicy<>();
		triggeringPolicy.setContext(loggerContext);
		triggeringPolicy.setParent(appender);
		triggeringPolicy.setMaxFileSize(maxSize);
		triggeringPolicy.setMaxHistory(maxHistory);
		triggeringPolicy.setFileNamePattern(pattern);
		triggeringPolicy.setTotalSizeCap(maxTotalSize);
		triggeringPolicy.start();

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(loggerContext);
		encoder.setPattern("%d %msg%n");
		encoder.start();

		appender.setEncoder(encoder);
		appender.setRollingPolicy(rollingPolicy);
		appender.setTriggeringPolicy(triggeringPolicy);
		appender.start();

		logger = loggerContext.getLogger(getId());
		logger.addAppender(appender);
	}

	@Override
	public void consume(Payload payload) {
		if (logger != null) {
			String output = processorService.getScriptService().jsonStringify(payload.getValue());
			logger.info(output);
		}
	}

}
