package com.untrackr.alerter.processor.producer.df;

import com.untrackr.alerter.common.ScriptObject;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

import java.io.File;

public class Df extends ScheduledProducer {

	private File file;
	private boolean fileNotFoundErrorSignaled = false;

	public Df(ProcessorService processorService, IncludePath path, ScheduledExecutor scheduledExecutor, File file) {
		super(processorService, path, scheduledExecutor);
		this.file = file;
	}

	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	protected Object produce() {
		PartitionInfo info = new PartitionInfo();
		info.file = file.getAbsolutePath();
		if (!file.exists()) {
			if (fileNotFoundErrorSignaled) {
				return null;
			} else {
				fileNotFoundErrorSignaled = true;
				throw new RuntimeProcessorError("file not found: " + file, this, null);
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
		return info;
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
