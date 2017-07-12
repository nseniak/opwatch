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

import ch.qos.logback.core.util.FileSize;
import org.opwatch.documentation.ProcessorCategory;
import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.FactoryExecutionScope;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.service.ProcessorService;

public class LogFactory extends ActiveProcessorFactory<LogConfig, Log> {

	public LogFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "log";
	}

	@Override
	public Class<LogConfig> configurationClass() {
		return LogConfig.class;
	}

	@Override
	public Class<Log> processorClass() {
		return Log.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeConsumer();
	}

	@Override
	public ProcessorCategory processorCategory() {
		return ProcessorCategory.consumer;
	}

	@Override
	public Log make(Object scriptObject) {
		LogConfig config = convertProcessorConfig(scriptObject);
		String file = checkPropertyValue("file", config.getFile());
		FileSize maxSize = checkFileSize(checkPropertyValue("maxSize", config.getMaxSize()));
		FileSize maxTotalSize = checkFileSize(checkPropertyValue("maxTotalSize", config.getMaxTotalSize()));
		int maxHistory = checkPropertyValue("maxHistory", config.getMaxHistory());
		if (maxHistory <= 0) {
			throw new RuntimeError("wrong value for \"maxHistory\", must be a positive number: " + maxHistory, new FactoryExecutionScope(this));
		}
		String compression = checkPropertyValue("compression", config.getCompression()).toLowerCase();
		if (!(compression.isEmpty() || compression.equals("gz") || compression.equals("zip"))) {
			throw new RuntimeError("wrong value for \"compression\": \"" + compression + "\"", new FactoryExecutionScope(this));
		}
		return new Log(getProcessorService(), config, name(), file, maxSize, maxTotalSize, maxHistory, compression);
	}

	private FileSize checkFileSize(String fileSize) {
		try {
			return FileSize.valueOf(fileSize);
		} catch (Exception e) {
			throw new RuntimeError("text cannot be parsed to a size: " + fileSize, new FactoryExecutionScope(this), e);
		}
	}


}
