package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
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
		info.setFile(file.getAbsolutePath());
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
		info.setSize(partitionSize);
		long partitionAvailable = file.getFreeSpace();
		info.setAvailable(partitionAvailable);
		long partitionUsed = partitionSize - partitionAvailable;
		info.setUsed(partitionUsed);
		info.setPercentUsed(((double) partitionUsed * 100) / partitionSize);
		return info;
	}

	@Override
	public String identifier() {
		return file.toString();
	}

	public static class PartitionInfo {

		private String file;
		private Long size;
		private Long used;
		private Long available;
		private Double percentUsed;

		public String getFile() {
			return file;
		}

		public void setFile(String file) {
			this.file = file;
		}

		public Long getSize() {
			return size;
		}

		public void setSize(Long size) {
			this.size = size;
		}

		public Long getUsed() {
			return used;
		}

		public void setUsed(Long used) {
			this.used = used;
		}

		public Long getAvailable() {
			return available;
		}

		public void setAvailable(Long available) {
			this.available = available;
		}

		public Double getPercentUsed() {
			return percentUsed;
		}

		public void setPercentUsed(Double percentUsed) {
			this.percentUsed = percentUsed;
		}

	}

}
