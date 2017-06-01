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
