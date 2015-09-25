package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.service.ProcessorService;

import java.io.File;
import java.util.Date;

public class Stat extends ScheduledProducer {

	private File file;

	public Stat(ProcessorService processorService, IncludePath path, ScheduledExecutor scheduledExecutor, File file) {
		super(processorService, path, scheduledExecutor);
		this.file = file;
	}

	@Override
	protected Object produce() {
		FileInfo info = new FileInfo();
		info.setFile(file.getAbsolutePath());
		if (!file.exists()) {
			info.setExists(false);
		} else {
			info.setExists(true);
			info.setSize(file.length());
			info.setLastModified(new Date(file.lastModified()));
			long partitionSize = file.getTotalSpace();
			info.setPartitionSize(partitionSize);
			long partitionAvailable = file.getFreeSpace();
			info.setPartitionAvailable(partitionAvailable);
			long partitionUsed = partitionSize - partitionAvailable;
			info.setPartitionUsed(partitionUsed);
			info.setPartitionPercentUsed(((double) partitionUsed * 100) / partitionSize);
		}
		return info;
	}

	public static class FileInfo {

		private String file;
		private boolean exists;
		private Long size;
		private Date lastModified;
		private Long partitionSize;
		private Long partitionUsed;
		private Long partitionAvailable;
		private Double partitionPercentUsed;

		public String getFile() {
			return file;
		}

		public void setFile(String file) {
			this.file = file;
		}

		public boolean isExists() {
			return exists;
		}

		public void setExists(boolean exists) {
			this.exists = exists;
		}

		public Long getSize() {
			return size;
		}

		public void setSize(Long size) {
			this.size = size;
		}

		public Date getLastModified() {
			return lastModified;
		}

		public void setLastModified(Date lastModified) {
			this.lastModified = lastModified;
		}

		public Long getPartitionSize() {
			return partitionSize;
		}

		public void setPartitionSize(Long partitionSize) {
			this.partitionSize = partitionSize;
		}

		public Long getPartitionUsed() {
			return partitionUsed;
		}

		public void setPartitionUsed(Long partitionUsed) {
			this.partitionUsed = partitionUsed;
		}

		public Long getPartitionAvailable() {
			return partitionAvailable;
		}

		public void setPartitionAvailable(Long partitionAvailable) {
			this.partitionAvailable = partitionAvailable;
		}

		public Double getPartitionPercentUsed() {
			return partitionPercentUsed;
		}

		public void setPartitionPercentUsed(Double partitionPercentUsed) {
			this.partitionPercentUsed = partitionPercentUsed;
		}

	}

}
