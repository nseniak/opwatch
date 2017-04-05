package com.untrackr.alerter.ioservice;

import com.untrackr.alerter.service.AlerterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class TailedFile {

	private static final Logger logger = LoggerFactory.getLogger(TailedFile.class);

	private Path file;
	private TailLineHandler handler;
	private AlerterConfig alerterConfig;

	public TailedFile(AlerterConfig alerterConfig, Path file, TailLineHandler handler) {
		this.alerterConfig = alerterConfig;
		this.file = file;
		this.handler = handler;
	}

	public void tail() throws InterruptedException, IOException {
		LineReader reader = null;
		try {
			while (true) {
				int lineNumber = 0;
				BasicFileAttributes currentAttributes;
				if (((currentAttributes = fileAttributes(file)) == null) || ((reader = openFile(file)) == null)) {
					logger.info("Waiting for file: " + file);
					while (((currentAttributes = fileAttributes(file)) == null) || ((reader = openFile(file)) == null)) {
						Thread.sleep(alerterConfig.getTailedFileWatchingCheckDelay());
					}
					logger.info("File created: " + file);
				} else {
					// File already exists. Go to the tail.
					while (reader.readLine() != null) {
						lineNumber = lineNumber + 1;
						// Continue
					}
				}
				while (true) {
					BasicFileAttributes attrs = fileAttributes(file);
					if (attrs == null) {
						logger.info("File deleted: " + file);
						safeClose(reader);
						break;
					}
					if (!attrs.fileKey().equals(currentAttributes.fileKey())) {
						// File has changed
						logger.info("File location has changed: " + file);
						safeClose(reader);
						break;
					}
					if (attrs.size() < currentAttributes.size()) {
						// File has been truncated
						logger.info("File truncated: " + file);
						safeClose(reader);
						break;
					}
					currentAttributes = attrs;
					String line = reader.readLine();
					if (line == null) {
						// end of file, start polling
						Thread.sleep(alerterConfig.getTailPollInterval());
					} else {
						lineNumber = lineNumber + 1;
						handler.handle(line, lineNumber);
					}
				}
			}
		} finally {
			if (reader != null) {
				safeClose(reader);
			}
		}
	}

	private BasicFileAttributes fileAttributes(Path path) {
		try {
			return Files.readAttributes(file, BasicFileAttributes.class);
		} catch (IOException e) {
			return null;
		}
	}

	private void safeClose(LineReader reader) {
		try {
			reader.close();
		} catch (IOException e) {
			// Do nothing
		}
	}

	private LineReader openFile(Path path) {
		try {
			InputStream in = Files.newInputStream(path);
			return new LineReader(new BufferedInputStream(in), alerterConfig.getLineBufferSize(), false);
		} catch (IOException e) {
			return null;
		}
	}

	public Path getFile() {
		return file;
	}

	public void setFile(Path file) {
		this.file = file;
	}

	public TailLineHandler getHandler() {
		return handler;
	}

}