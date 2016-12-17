package com.untrackr.alerter.processor.producer.stat;

import com.untrackr.alerter.common.ScriptObject;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

import java.io.File;
import java.util.Date;

public class Stat extends ScheduledProducer {

	private File file;

	public Stat(ProcessorService processorService, StatDesc descriptor, String name, ScheduledExecutor scheduledExecutor, File file) {
		super(processorService, descriptor, name, scheduledExecutor);
		this.file = file;
	}

	@Override
	protected void produce() {
		FileInfo info = new FileInfo(processorService);
		info.file = file.getAbsolutePath();
		if (!file.exists()) {
			info.exists = false;
		} else {
			info.exists = true;
			info.size = file.length();
			info.lastModified = new Date(file.lastModified());
		}
		outputProduced(info);
	}

	public static class FileInfo extends ScriptObject {

		private String file;
		private boolean exists;
		private Long size;
		private Date lastModified;

		public FileInfo(ProcessorService processorService) {
			super(processorService);
		}

		public String getFile() {
			return file;
		}

		public boolean isExists() {
			return exists;
		}

		public Long getSize() {
			return size;
		}

		public Date getLastModified() {
			return lastModified;
		}

	}

}
