package com.untrackr.alerter.processor.primitives.producer.df;

import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.processor.common.ProcessorVoidExecutionScope;
import com.untrackr.alerter.processor.payload.PayloadObjectValue;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.primitives.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

import java.io.File;

public class Df extends ScheduledProducer<DfConfig> {

	private File file;
	private boolean fileNotFoundErrorSignaled = false;

	public Df(ProcessorService processorService, DfConfig descriptor, String name, ScheduledExecutor scheduledExecutor, File file) {
		super(processorService, descriptor, name, scheduledExecutor);
		this.file = file;
	}

	@Override
	protected void produce() {
		PartitionInfo info = new PartitionInfo();
		info.file = file.getAbsolutePath();
		if (!file.exists()) {
			if (fileNotFoundErrorSignaled) {
				return;
			} else {
				fileNotFoundErrorSignaled = true;
				throw new RuntimeError("file not found: " + file, new ProcessorVoidExecutionScope(this));
			}
		}
		fileNotFoundErrorSignaled = false;
		long partitionSize = file.getTotalSpace();
		info.size = partitionSize;
		long partitionAvailable = file.getFreeSpace();
		info.available = partitionAvailable;
		long partitionUsed = partitionSize - partitionAvailable;
		info.used = partitionUsed;
		info.percentUsed = ((double) partitionUsed * 100) / partitionSize;
		outputProduced(info);
	}

	public static class PartitionInfo extends PayloadObjectValue {

		private String file;
		private Long size;
		private Long used;
		private Long available;
		private Double percentUsed;

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

		public Double getPercentUsed() {
			return percentUsed;
		}

	}

}
