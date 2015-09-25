package com.untrackr.alerter.ioservice;

import com.google.common.io.PatternFilenameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class WatchedDirectory {

	private static final Logger logger = LoggerFactory.getLogger(WatchedDirectory.class);

	/**
	 * The directory to watch
	 */
	private File directory;
	/**
	 * The pattern of the files to watch
	 */
	private Pattern filePattern;
	/**
	 * Watched directory files.
	 */
	private Map<String, WatchedFile> watchedFiles = new ConcurrentHashMap<>();
	/**
	 * Invoked when the a file event occurs.
	 */
	private FileEventHandler fileEventHandler;
	/**
	 * Service used to watch the files.
	 */
	private FileWatchingService fileWatchingService;

	public WatchedDirectory(File directory, String fileRegex, FileWatchingService fileWatchingService, FileEventHandler fileEventHandler) throws FileNotFoundException {
		if (!directory.exists()) {
			throw new FileNotFoundException(directory.toString());
		} else if (!directory.isDirectory()) {
			throw new IllegalArgumentException("not a directory: " + directory);
		}
		this.directory = directory;
		this.filePattern = Pattern.compile(fileRegex);
		this.fileWatchingService = fileWatchingService;
		this.fileEventHandler = fileEventHandler;
	}

	public class FileEventHandlerWrapper implements FileEventHandler {

		@Override
		public void handleContent(WatchedFile watchedFile) {
			fileEventHandler.handleContent(watchedFile);
		}

		@Override
		public void handleDelete(WatchedFile watchedFile) {
			watchedFiles.remove(watchedFile.getFile().getPath());
			fileWatchingService.removeWatchedFile(watchedFile);
			logger.info("File deleted: " + watchedFile.getFile());
			fileEventHandler.handleDelete(watchedFile);
		}

	}

	public void watch() throws FileNotFoundException {
		File[] files = directory.listFiles(new PatternFilenameFilter(filePattern));
		for (File file : files) {
			if (!watchedFiles.containsKey(file)) {
				WatchedFile watchedFile = new WatchedFile(file, new FileEventHandlerWrapper(), false);
				watchedFiles.put(file.getPath(), watchedFile);
				logger.info("File created: " + watchedFile.getFile());
				fileWatchingService.addWatchedFile(watchedFile);
			}
		}
	}

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}

}
