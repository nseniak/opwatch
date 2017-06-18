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

package org.opwatch.processor.primitives.producer.df;

import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.processor.payload.PayloadPojoValue;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;

import java.io.File;

public class Df extends ScheduledProducer<DfConfig> {

	private File file;

	public Df(ProcessorService processorService, DfConfig configuration, String name, ScheduledExecutor scheduledExecutor, File file) {
		super(processorService, configuration, name, scheduledExecutor);
		this.file = file;
	}

	@Override
	protected void produce() {
		FilesystemInfo info = new FilesystemInfo();
		info.file = file.getAbsolutePath();
		if (!file.exists()) {
			throw new RuntimeError("file not found: " + file, new ProcessorVoidExecutionScope(this));
		}
		long partitionSize = file.getTotalSpace();
		info.size = partitionSize;
		long partitionAvailable = file.getFreeSpace();
		info.available = partitionAvailable;
		long partitionUsed = partitionSize - partitionAvailable;
		info.used = partitionUsed;
		info.usageRatio = ((double) partitionUsed) / partitionSize;
		outputProduced(info.toJavascript(processorService.getScriptService()));
	}

	public static class FilesystemInfo extends PayloadPojoValue {

		private String file;
		private Long size;
		private Long used;
		private Long available;
		private Double usageRatio;

		public String getFile() {
			return file;
		}

		public Long getSize() {
			return size;
		}

		public Long getUsed() {
			return used;
		}

		public Long getAvailable() {
			return available;
		}

		public Double getUsageRatio() {
			return usageRatio;
		}

	}

}
