package org.opwatch.ioservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;

public class WatchedFile {

	private static final Logger logger = LoggerFactory.getLogger(FileWatchingService.class);

	/**
	 * The file to watch
	 */
	private File file;
	/**
	 * Last date at which the file was read, or 0 if the file doesn't exist.
	 */
	private long lastExistingFileNotificationDate;
	/**
	 * Invoked when the a file event occurs.
	 */
	private FileEventHandler fileEventHandler;

	public WatchedFile(File file, FileEventHandler fileEventHandler, boolean mustExist) throws FileNotFoundException {
		this.file = file;
		this.fileEventHandler = fileEventHandler;
		if (file.exists()) {
			this.lastExistingFileNotificationDate = file.lastModified();
		} else {
			if (mustExist) {
				throw new FileNotFoundException(file.toString());
			}
			this.lastExistingFileNotificationDate = 0;
		}
	}

	public void watch() {
		if (!file.exists()) {
			if (lastExistingFileNotificationDate != 0) {
				// File has been deleted
				lastExistingFileNotificationDate = 0;
				logger.info("File deleted: " + file);
				fileEventHandler.handleDelete(this);
			}
			return;
		}
		if (lastExistingFileNotificationDate == 0) {
			// File has been created
			lastExistingFileNotificationDate = file.lastModified();
			logger.info("File created: " + file);
			fileEventHandler.handleContent(this);
			return;
		}
		long lastModified = file.lastModified();
		if (lastModified > lastExistingFileNotificationDate) {
			// File has been modified
			lastExistingFileNotificationDate = lastModified;
			logger.info("File modified: " + file);
			fileEventHandler.handleContent(this);
		}
	}

	public boolean changed() {
		return (file.exists() && (file.lastModified() > lastExistingFileNotificationDate));
	}

	@Override
	public String toString() {
		return file.toString();
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getLastExistingFileNotificationDate() {
		return lastExistingFileNotificationDate;
	}

	public void setLastExistingFileNotificationDate(long lastExistingFileNotificationDate) {
		this.lastExistingFileNotificationDate = lastExistingFileNotificationDate;
	}

	public FileEventHandler getFileEventHandler() {
		return fileEventHandler;
	}

	public void setFileEventHandler(FileEventHandler fileEventHandler) {
		this.fileEventHandler = fileEventHandler;
	}

}
