package com.untrackr.alerter.processor.producer.df;

import com.untrackr.alerter.common.ScriptObject;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ProcessorExecutionException;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

import java.io.File;

public class Df extends ScheduledProducer {

	private File file;
	private boolean fileNotFoundErrorSignaled = false;

	public Df(ProcessorService processorService, ScriptStack stack, ScheduledExecutor scheduledExecutor, File file) {
		super(processorService, stack, scheduledExecutor);
		this.file = file;
	}

	@Override
	protected void produce() {
		PartitionInfo info = new PartitionInfo(processorService);
		info.file = file.getAbsolutePath();
		if (!file.exists()) {
			if (fileNotFoundErrorSignaled) {
				return;
			} else {
				fileNotFoundErrorSignaled = true;
				throw new ProcessorExecutionException("file not found: " + file, this, null);
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

	@Override
	public String identifier() {
		return file.toString();
	}

	public static class PartitionInfo extends ScriptObject {

		private String file;
		private Long size;
		private Long used;
		private Long available;
		private Double percentUsed;

		public PartitionInfo(ProcessorService processorService) {
			super(processorService);
		}

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
