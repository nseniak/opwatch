package com.untrackr.alerter.ioservice;

import com.untrackr.alerter.model.common.AlerterProfile;
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
	private AlerterProfile alerterProfile;

	public TailedFile(AlerterProfile alerterProfile, Path file, TailLineHandler handler) {
		this.alerterProfile = alerterProfile;
		this.file = file;
		this.handler = handler;
	}

	public void tail() throws InterruptedException, IOException {
		Reader reader = null;
		try {
			while (true) {
				BasicFileAttributes currentAttributes;
				if (((currentAttributes = fileAttributes(file)) == null) || ((reader = openFile(file)) == null)) {
					logger.info("Waiting for file: " + file);
					while (((currentAttributes = fileAttributes(file)) == null) || ((reader = openFile(file)) == null)) {
						Thread.sleep(alerterProfile.getTailedFileWatchingCheckDelay());
					}
					logger.info("File created: " + file);
				}
				int lineNumber = 0;
				BufferedReader buffered = new BufferedReader(reader);
				// Go to the tail
				while (buffered.readLine() != null) {
					lineNumber = lineNumber + 1;
					// Continue
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
					String line = buffered.readLine();
					if (line == null) {
						// end of file, start polling
						Thread.sleep(alerterProfile.getTailPollInterval());
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

	private void safeClose(Reader reader) {
		try {
			reader.close();
		} catch (IOException e) {
			// Do nothing
		}
	}

	private Reader openFile(Path path) {
		try {
			InputStream in = Files.newInputStream(path);
			return new BufferedReader(new InputStreamReader(in));
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

}