package org.opwatch.processor.primitives.producer.stat;

import org.opwatch.processor.payload.PayloadPojoValue;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;

import java.io.File;

public class Stat extends ScheduledProducer<StatConfig> {

	private File file;

	public Stat(ProcessorService processorService, StatConfig configuration, String name, ScheduledExecutor scheduledExecutor, File file) {
		super(processorService, configuration, name, scheduledExecutor);
		this.file = file;
	}

	@Override
	protected void produce() {
		FileInfo info = new FileInfo();
		info.file = file.getAbsolutePath();
		if (!file.exists()) {
			info.exists = false;
		} else {
			info.exists = true;
			info.size = file.length();
			info.lastModified = file.lastModified();
		}
		outputProduced(info.toJavascript(processorService.getScriptService()));
	}

	public static class FileInfo extends PayloadPojoValue {

		private String file;
		private boolean exists;
		private Long size;
		private long lastModified;

		public String getFile() {
			return file;
		}

		public boolean isExists() {
			return exists;
		}

		public Long getSize() {
			return size;
		}

		public long getLastModified() {
			return lastModified;
		}

	}

}
